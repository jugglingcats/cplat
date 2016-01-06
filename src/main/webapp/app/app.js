(function() {
    var app = angular.module("cplat", ['oc.lazyLoad', 'dynamic.module', 'cplat-login', 'angularSpinner', 'ngMaterial']);

    app.config(['$ocLazyLoadProvider', '$dynamicModuleProvider', '$stateProvider', function ($ocLazyLoadProvider, $dynamicModuleProvider, $stateProvider) {
        $ocLazyLoadProvider.config({
            debug: true,
            events: true
        });

        console.log("Initialising container application");

        $stateProvider.state("app", {
            url: "",
            views: {
                "": {
                    templateUrl: "core/index.html",
                    controller: "CoreCtrl"
                },
                "rightNav": {
                    template: "APP STATE"
                }
            },
            resolve: {
                test: function ($ocLazyLoad) {
                    console.log("Resolve for test");
                    return $ocLazyLoad.load({
                        name: "cplat-core",
                        files: ["core/module.js"]
                    })
                }
            }
        });
        $stateProvider.state("404", {
            url: "/404",
            templateUrl: "core/404.html"
        });

        $dynamicModuleProvider.configure(["item", "facet", "app2"], "/404");
    }]);

    app.controller("HeadCtrl", function ($scope, $state, SessionManager) {
        $scope.session = SessionManager.session();

        $scope.transition = function(state) {
            $state.transitionTo(state);
        }
    });
})();

