(function() {
    var app=angular.module("item", ['cplat-facets']);

    app.factory("ItemService", function($http, $q) {
        var def=$q.defer();
        $http.get("/api/item/list").success(function(d) {
            def.resolve(d);
        }).error(function(e) {
            def.reject(e);
        });
        return {
            'list': function() {
                return def.promise;
            },
            'search': function(q) {
                var qd=$q.defer();
                $http.get("/api/item/search?q="+q).success(function(d) {
                    console.log("Result!", d);
                    qd.resolve(d);
                }).error(function(e) {
                    qd.reject(e);
                });
                return qd.promise;
            },
            'get': function(id) {
                return $http.get("/api/item/"+id);
            },
            'upvote': function(id) {
                return $http.post("/api/item/"+id+"/upvote");
            }
        }
    });

    app.controller("ItemDashboardCtrl", function($scope, ItemService) {
        function reset() {
            ItemService.list().then(function(d) {
                $scope.items=d;
            });
        }

        $scope.$watch("searchText", function(newVal, oldVal) {
            if ( newVal || oldVal ) {
                if (!newVal) {
                    reset();
                } else {
                    ItemService.search(newVal).then(function(d) {
                        $scope.items=d.map(function(item) {
                            return item.source;
                        });
                    })
                }
            }
        });

        reset();
    });

    app.controller("ItemDetailCtrl", function($scope, $stateParams, $sce, ItemService) {
        var itemId = $stateParams.itemId;
        ItemService.get(itemId).success(function(d) {
            $scope.item=d;
            $scope.content=$sce.trustAsHtml(d.content);
        });

        $scope.upvote = function() {
            ItemService.upvote(itemId).success(function(votes) {
                $scope.item.votes=votes;
            })
        }
    });

    app.controller("ItemCreateCtrl", function($scope, $state, $http) {
        $scope.create=function() {
            console.log("Creating: ", $scope.title);
            $http.post("/api/item", { title: $scope.title, prop1: "testing" }).success(function() {console.log("Done!")});
            $state.transitionTo("item.dashboard");
        }
    });
}());