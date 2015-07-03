(function() {
    var app=angular.module("item", []);

    app.factory("ItemService", function($http, $q) {
        var def=$q.defer();
        $http.get("/api/item/list").success(function(d) {
            def.resolve(d);
        }).error(function(e) {
            def.cancel(e);
        });
        return {
            'list': function() {
                return def.promise;
            },
            'get': function(id) {
                return $http.get("/api/item/"+id);
            }
        }
    });

    app.controller("ItemDashboardCtrl", function($scope, ItemService) {
        ItemService.list().then(function(d) {
            $scope.items=d;
        });
    });

    app.controller("ItemDetailCtrl", function($scope, $stateParams, $sce, ItemService) {
        var itemId = $stateParams.itemId;
        ItemService.get(itemId).success(function(d) {
            $scope.item=d;
            $scope.content=$sce.trustAsHtml(d.content);
        });
    });

    app.controller("ItemCreateCtrl", function($scope, $state, $http) {
        $scope.create=function() {
            console.log("Creating: ", $scope.title);
            $http.post("/api/item", { title: $scope.title, prop1: "testing" }).success(function() {console.log("Done!")});
            $state.transitionTo("item.dashboard");
        }
    });
}());