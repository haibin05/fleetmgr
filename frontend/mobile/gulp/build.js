'use strict';

var gulp = require('gulp');

var $ = require('gulp-load-plugins')();
var saveLicense = require('uglify-save-license');
var gulp = require('gulp');
var gulpFilter = require('gulp-filter');
var useref = require('gulp-useref');

gulp.task('styles', function () {
  return $.rubySass('app/styles/', {
      style: 'expanded',
      loadPath: ['app/bower_components/bootstrap-sass-official/assets/stylesheets']
    }).on('error', function (err) {
        console.error('Error!', err.message);
    })
    .pipe($.plumber())
    .pipe($.autoprefixer('last 1 version'))
    .pipe(gulp.dest('.tmp/styles'))
    .pipe($.size());
});

gulp.task('scripts', function () {
  return gulp.src('app/scripts/**/*.js')
    .pipe($.jshint())
    .pipe($.jshint.reporter('jshint-stylish'))
    .pipe($.size());
});

gulp.task('partials', function () {
  //return gulp.src('app/partials/**/*.html')
  return gulp.src(['app/partials/**/*.html', 'app/index.html'])
    .pipe($.minifyHtml({
      empty: true,
      spare: true,
      quotes: true
    }))
    .pipe(gulp.dest("dist/partials"))
    .pipe($.size());
});

gulp.task('html', ['styles', 'scripts', 'partials'], function () {
  var jsFilter = gulpFilter('**/*.js', {restore: true});
  var cssFilter = gulpFilter('**/*.css', {restore: true});
  var userefAssets = useref.assets();

  return gulp.src('app/*.html')
    .pipe(userefAssets)
    .pipe($.rev())
    .pipe(jsFilter)
    .pipe($.ngmin())
    .pipe($.uglify({preserveComments: saveLicense}))
    .pipe(jsFilter.restore)
    .pipe(cssFilter)
    .pipe($.replace('bower_components/mobile-angular-ui/dist/fonts','fonts'))
    .pipe($.csso())
    .pipe(cssFilter.restore)
    .pipe(userefAssets.restore())
    .pipe(useref())
    .pipe($.revReplace())
    .pipe(gulp.dest('dist'))
    .pipe($.size());
});

gulp.task('images', function () {
  return gulp.src('app/images/**/*')
    //.pipe($.cache($.imagemin({
    //  optimizationLevel: 3,
    //  progressive: true,
    //  interlaced: true
    //})))
    .pipe(gulp.dest('dist/images'))
    .pipe($.size());
});

gulp.task('fonts', function () {
   return gulp.src('app/bower_components/**/*')
     .pipe($.filter('**/*.{eot,svg,ttf,woff,woff2}'))
     .pipe($.flatten())
     .pipe(gulp.dest('dist/fonts'))
     .pipe($.size());
 });

gulp.task('clean', function () {
  return gulp.src(['.tmp', 'dist'], { read: false }).pipe($.clean());
});

gulp.task('build', ['html', 'partials', 'images', 'fonts']);
