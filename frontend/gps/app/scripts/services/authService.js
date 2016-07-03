'use strict';

angular.module('gpsApp')
    .factory('Auth',['$location', '$rootScope', 'Restangular', 'User', '$cookies','$q', function Auth($location, $rootScope, Restangular, User, $cookies,$q) {
        var currentUser = {};
        if ($cookies.get('token')) {
            User.get()
                .then(function(user){
                    currentUser = user;
                });
        }

        return {

            /**
             * Authenticate user and save token
             *
             * @param  {Object}   user     - login info
             * @param  {Function} callback - optional
             * @return {Promise}
             */
            login: function (user, callback) {
                var cb = callback || angular.noop;


                return Restangular.one("auth/local").customPOST(
                    {
                        userName: user.userName,
                        password: user.password
                    },
                    '',{},{}).then(function (data) {
                        $cookies.put('token', data.token);
                        return User.get()
                            .then(function(user){
                                currentUser = user;
                                $rootScope.$broadcast("login.event");
                                cb();
                            });

                    }).catch(function (err) {
                        this.logout();
                        cb(err);
                        return $q.reject(err);
                    }.bind(this));

            },

            /**
             * Delete access token and user info
             *
             * @param  {Function}
             */
            logout: function () {
                $cookies.remove('token');
                currentUser = {};
                $rootScope.$broadcast("logout.event");
            },


            /**
             * Gets all available info on authenticated user
             *
             * @return {Object} user
             */
            getCurrentUser: function () {
                return currentUser;
            },

            /**
             * Check if a user is logged in
             *
             * @return {Boolean}
             */
            isLoggedIn: function () {
                return currentUser.hasOwnProperty('roles');
            },



            /**
             * Check if a user is an admin
             *
             * @return {Boolean}
             */
            isAdmin: function () {
                return currentUser.roles.indexOf('admin')!=-1;
            },

            /**
             * Get auth token
             */
            getToken: function () {
                return $cookies.get('token');
            }
        };
    }]);
