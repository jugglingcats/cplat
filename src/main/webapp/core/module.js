(function () {
    var app = angular.module("test", []);
    app.controller("TestCtrl", function ($scope, $dynamicMenu) {
        $scope.msg="Hello";
    });
}());