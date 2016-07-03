'use strict';

var gulp = require('gulp');

var browserSync = require('browser-sync');
var httpProxy = require('http-proxy');
var gutil = require('gulp-util');
var url=require('url')
var tunnel = require('tunnel');

/* This configuration allow you to configure browser sync to proxy your backend */
var proxyTarget = process.env.FLEET_SERVER ||  'http://xietong110.com'; // The location of your backend

var proxyApiPrefix = '/api/'; // The element in the URL which differentiate between API request and static file request


var proxyUrl= process.env.http_proxy||process.env.HTTP_PROXY;
var tunnelingAgent =null;
if (!!proxyUrl){
  proxy=url.parse(proxyUrl);
  gutil.log("Use the proxy at", gutil.colors.yellow(JSON.stringify(proxy) ));
  tunnelingAgent = tunnel.httpOverHttp({

    proxy: { // Proxy settings
      host: proxy.hostname,
      port: proxy.port||80
    }
  });
}


var proxy = httpProxy.createProxyServer({
  target: proxyTarget,
  agent:tunnelingAgent,
  changeOrigin:true
});

proxy.on('upgrade', function (req, socket, head) {
  proxy.ws(req, socket, head);
});

function proxyMiddleware(req, res, next) {
  if (req.url.indexOf(proxyApiPrefix) !== -1 || req.url.indexOf('/events') !== -1) {
    proxy.web(req, res);
  } else {
    next();
  }
}


function browserSyncInit(baseDir, files, browser) {
  browser = browser === undefined ? 'default' : browser;

  browserSync.instance = browserSync.init(files, {
    startPath: '/',
    server: {
      baseDir: baseDir,
      middleware: proxyMiddleware
    },
    browser: browser
  });
  gutil.log("Use the api server at", gutil.colors.yellow(proxyTarget));
  if (!!proxyUrl){
    gutil.log("Use the proxy at", gutil.colors.yellow(proxyUrl));
  }

}

gulp.task('serve', ['watch'], function () {
  browserSyncInit([
    'app',
    '.tmp'
  ], [
    'app/*.html',
    '.tmp/styles/**/*.css',
    'app/scripts/**/*.js',
    'app/partials/**/*.html',
    'app/images/**/*'
  ]);
});

gulp.task('serve:dist', ['build'], function () {
  browserSyncInit('dist');
});

gulp.task('serve:e2e', function () {
  browserSyncInit(['app', '.tmp'], null, []);
});

gulp.task('serve:e2e-dist', ['watch'], function () {
  browserSyncInit('dist', null, []);
});

