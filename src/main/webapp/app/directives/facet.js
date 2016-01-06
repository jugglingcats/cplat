(function () {
    var app = angular.module("cplat-facets", ['cplat-data']);

    app.directive("itemFacets", function(DataService) {
        return {
            target: 'A',
            require: 'ngModel',
            templateUrl: "app/templates/facet-edit.html",
            link: function($scope, elem, attrs, ngModel) {
                $scope.selection = {};
                $scope.values = {};

                function refresh() {
                    DataService.validFacets($scope.selection).then(function (d) {
                        console.log("Types:", d);
                        $scope.options = d;
                        angular.forEach(d, function (t) {
                            DataService.valuesByType(t.code).then(function (v) {
                                $scope.values[t.code] = v;
                            });
                        });
                    });
                }

                ngModel.$render=function() {
                    $scope.selection=ngModel.$viewValue || {};
                    refresh();
                };

                $scope.select = function(code, item) {
                    $scope.selection[code]=item.id;
                };

                $scope.$watch("selection", function (newVal) {
                    ngModel.$setViewValue(newVal);
                    refresh();
                }, true);

                $scope.getValues = function(code) {
                    console.log("Get values: ", code);
                    return $scope.values[code];
                }
            }
        }
    })
}());
