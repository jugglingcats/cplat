(function () {
    var app = angular.module("cplat-login", ['persona', 'ngCookies']);

    app.factory("SessionManager", function (Persona, $cookies, $http) {
        var session = {
            state: 'unknown'
        };

        Persona.watch({
            onlogin: function (assertion) {
                $http.post("/api/session", {
                    assertion: assertion
                }).then(function (r) {
                    console.log("Login successful!", r.data);
                    session.state = 'authenticated';
                    session.account = r.data;
                });
            }
        });

        // onload check current state
        var sid = $cookies.get("sid");
        if (sid) {
            $http.get("/api/session/" + sid).then(function (r) {
                session.state = 'authenticated';
                session.account = r.data;
            }, function (e) {
                session.state = 'loggedout';
                delete session.account;
            })
        } else {
            session.state='loggedout';
        }

        return {
            session: function () {
                return session;
            }
        }
    });

    app.directive("login", function (Persona) {
        return {
            restrict: 'A',
            link: function (scope, elem, attrs) {
                elem.bind("click", function () {
                    Persona.request();
                });
            }
        }
    });
}());