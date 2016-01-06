(function () {
    var app = angular.module("facet", ['cplat-data']);

    app.controller("FacetCtrl", function ($scope, DataService) {
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

        $scope.$watch("selection", function (newVal) {
            refresh();
        }, true);

        $scope.refresh = refresh;
    });

    app.controller("FacetTypeCtrl", function ($scope, DataService) {
        $scope.def = {};

        function refreshTypes() {
            DataService.types.find().then(function (d) {
                $scope.types = d;
            });
        }

        refreshTypes();

        DataService.types.subscribe($scope, refreshTypes);

        $scope.upsert = function () {
            DataService.types.add($scope.def);
        };
        $scope.delete = function () {
            DataService.types.delete($scope.def);
        }
        $scope.reset = function () {
            $scope.def = {}
        };
    });

    app.controller("FacetValueCtrl", function ($scope, DataService) {
        DataService.types.find().then(function (d) {
            $scope.types = d;
        });

        function reset() {
            $scope.def = {
                linked: []
            }
        }

        function refreshValues() {
            DataService.values().then(function (d) {
                $scope.values = d;
            });
        }

        refreshValues();
        reset();

        $scope.upsert = function () {
            DataService.addValue($scope.def).then(function () {
                refreshValues();
            });
        };

        $scope.reset = reset;

        $scope.delete = function () {
            DataService.deleteValue($scope.def).then(function () {
                refreshValues();
            });
        };

        $scope.addLinked = function (t) {
            $scope.def.linked = $scope.def.linked || [];
            $scope.def.linked.push(t);
        };

        $scope.remove = function (code) {
            $scope.def.linked.splice($scope.def.linked.indexOf(code))
        };

    });
})();