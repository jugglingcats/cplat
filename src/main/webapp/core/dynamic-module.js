(function () {
    var app = angular.module("dynamic.module", ['ui.router', 'dynamic-menu']);

    app.provider('$dynamicModule', ['$stateProvider', '$urlRouterProvider', '$dynamicMenuProvider', function ($stateProvider, $urlRouterProvider, $dynamicMenuProvider) {
        var initInjector = angular.injector(['ng']);
        var $http = initInjector.get('$http');
        var $q = initInjector.get('$q');

        var defer = $q.defer(); // used to defer handling of unknown state
        var initDone = false;
        var moduleCount;

        /**
         * Resolve a given module's dependencies
         * @param moduleId The id of the module
         * @param config The definition of the module
         * @param loader The loader to use (ie. ocLazyLoad)
         * @returns Object Promise that is met when all dependencies are loaded
         */
        function resolveModule(moduleId, config, loader) {
            var promise = $q.when(1); // auto-resolved promise

            // load any required modules first
            if (config.requires) {
                console.log("Loading module dependencies: ", config.requires);
                for (var name in config.requires) {
                    // don't re-load existing modules
                    if (config.requires.hasOwnProperty(name)) {
                        if (!loader.isLoaded(name)) {
                            console.log("Loading dependency: ", name);
                            var info = {name: name, files: config.requires[name]};
                            // chain the promise
                            promise = promise.then(function () {
                                return loader.load(info);
                            });
                        }
                    }
                }
            }
            return promise.then(function () {
                // load the module's files (expected to include an angular module matching the module id)
                if (config.files) {
                    console.log("Loading module files: ", config.files, "for module:", moduleId);
                    return promise.then(function () {
                        return loader.load({
                            name: moduleId,
                            files: config.files
                        });
                    });
                }
            });
        }

        /**
         * Create states and menu config for a module
         * @param moduleId The id of the module
         * @param order The module order (used to determine menu order)
         */
        function defineModule(moduleId, order) {
            // make a request for the module.json based on the module id
            $http.get(moduleId + "/module.json").success(function (module) {
                console.log("Initialising micro app: ", module.name);

                // right now we don't support dependencies per state, only for the whole module
                var dependenciesLoaded;

                for (var i = 0; i < module.states.length; i++) {
                    var def = module.states[i];
                    var stateId = def.id; // the id of the state
                    var state = def.state; // the actual state definition

                    if (!state.abstract) {
                        state.resolve = state.resolve || {}; // ensure resolve property exists
                        state.resolve._dynload = ['$ocLazyLoad', function ($ocLazyLoad) {
                            // the same resolve is added to all states, but we only want to do it once
                            if (!dependenciesLoaded) {
                                console.log("Resolving dependencies for state: ", stateId);
                                dependenciesLoaded = true;
                                return resolveModule(moduleId, module, $ocLazyLoad);
                            }
                        }];
                    }

                    console.log("Adding state: ", stateId, ":", state);
                    $stateProvider.state(stateId, state);
                }

                // configure the menu
                $dynamicMenuProvider.add(module, order);

                moduleCount--;
                if (moduleCount == 0) {
                    console.log("All modules loaded");

                    // after this point we expect all states to be ready to use
                    initDone = true;

                    // finalise the menu
                    $dynamicMenuProvider.resolve();

                    defer.resolve(); // ready to attempt sync
                }
            });
        }

        /**
         * Configure dynamic module loading for the app (called once per app)
         * @param modules The list of modules to load
         * @param otherwise State to use if state is not recognised
         */
        this.configure = function (modules, otherwise) {
            // called when url (path) is not recognised
            $urlRouterProvider.otherwise(function ($injector, $location) {
                if (initDone) {
                    // don't create promise after initialisation
                    console.log("Unknown url: ", $location.path());
                    return otherwise;
                }
                console.log("Returning promise for requested state: ", $location.path());
                return $injector.invoke(function ($urlRouter) {
                    return defer.promise.then(function () {
                        $urlRouter.sync(); // url is ready to sync
                    });
                });
            });

            moduleCount = modules.length;
            for (var i = 0; i < modules.length; i++) {
                var moduleId = modules[i];
                console.log("Loading module definition for: ", moduleId);
                defineModule(moduleId, i);
            }
        };

        this.$get = function () {
            return {};
        };
    }]);
}());