(function() {
    var app=angular.module("item", []);

    app.controller("ItemDashboardCtrl", function($scope, $http) {
        $http.get("/api/item").success(function(d) {
            $scope.items=d;
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