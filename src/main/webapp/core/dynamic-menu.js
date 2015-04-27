(function () {
    var app = angular.module("dynamic-menu", []);

    app.provider('$dynamicMenu', function () {
        var initInjector = angular.injector(['ng']);
        var $q = initInjector.get('$q');

        var menuDefs = [];
        var defer = $q.defer();

        this.add = function (module, order) {
            if (!module.menu) {
                return;
            }
            console.log("Adding menu for module: ", module.menu, "order", order);
            menuDefs.push({order: order, items: module.menu});
        };

        this.resolve = function () {
            console.log("Resolving menu: ", menuDefs);
            menuDefs.sort(function (a, b) {
                return a.order - b.order;
            });
            var menu = [];
            for (var i = 0; i < menuDefs.length; i++) {
                menu = menu.concat(menuDefs[i].items);
            }
            console.log("Menu resolved: ", menu);
            defer.resolve(menu);
        };

        this.$get = function () {
            console.log("Returning menu: ", defer.promise);
            return defer.promise;
        }
    });

    app.directive("dynamicMenu", function ($dynamicMenu) {
        return {
            restrict: "EA",
            scope: true, // isolated scope
            replace: true,
            link: function ($scope) {
                $dynamicMenu.then(function (m) {
                    console.log("Setting menu on scope: ", m);
                    $scope.menu = m;
                    $scope.$digest();
                });
            },
            template: '<ul class="nav navbar-nav">' +
            '<li ui-sref-active="active" ng-repeat="m in menu">' +
            '<a ui-sref="{{m.state}}">{{m.title}}</a>' +
            '</li>' +
            '</ul>'
        }
    })
}());