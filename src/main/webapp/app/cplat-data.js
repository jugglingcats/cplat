(function () {
    var app = angular.module("cplat-data", []);

    var TYPES_CHANGED_EVENT="cplat.types.changed";

    app.factory("DataService", function ($rootScope, $http, $q) {
        function executeHttp(httpPromise, modification) {
            var defer=$q.defer();
            httpPromise.success(function(d) {
                defer.resolve(d);
                if ( modification ) {
                    $rootScope.$emit(TYPES_CHANGED_EVENT);
                }
            });
            return defer.promise;
        }

        return {
            types: {
                find: function() {
                    return executeHttp($http.get("/api/facet/type"));
                },
                add: function (def) {
                    return executeHttp($http.post("/api/facet/type", def), true);
                },
                delete: function (def) {
                    return executeHttp($http.delete("/api/facet/type/"+def.code), true);
                },
                subscribe: function(scope, callback) {
                    var handler = $rootScope.$on(TYPES_CHANGED_EVENT, callback);
                    scope.$on('$destroy', handler);
                }
            },

            validFacets: function(selection) {
                var defer=$q.defer();
                $http.post("/api/facet/selection", selection).success(function(d) {
                    defer.resolve(d);
                });
                return defer.promise;
            },
            values: function() {
                var defer=$q.defer();
                $http.get("/api/facet/value").success(function(d) {
                    defer.resolve(d);
                });
                return defer.promise;
            },
            valuesByType: function(code) {
                var defer=$q.defer();
                $http.get("/api/facet/value/"+code).success(function(d) {
                    defer.resolve(d);
                });
                return defer.promise;
            },

            addValue: function (def) {
                return $http.post("/api/facet/value", def);
            },
            deleteValue: function (def) {
                return $http.delete("/api/facet/value/"+def.id);
            }
        }
    });

}());