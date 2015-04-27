(function() {
    /**
     * Very simple module to provide a filter using moment.js to nicely format dates for display
     */
    var app=angular.module("ng-moment", []);

    app.filter('moment', function() {
        return function(dateString, format) {
            return moment(dateString).format(format);
        };
    });
}());