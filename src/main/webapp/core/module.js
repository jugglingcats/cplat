(function () {
    var app = angular.module("cplat-core", ['persona']);
    app.controller("CoreCtrl", function ($scope, $dynamicMenu, Persona) {
        $scope.login = function() {
            console.log("Hello");
            Persona.request();
        }
    });
}());