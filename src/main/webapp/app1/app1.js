(function() {
    var app=angular.module("app1", []);

    app.controller("App1Ctrl", function($scope) {
        $scope.msg="Hello from App1Ctrl!!";
        $scope.now=new Date();
    })
}());