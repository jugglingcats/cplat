var gulp = require('gulp'),
    path = require('path'),
    changed = require('gulp-changed'),
    expect = require('gulp-expect-file'),
    glob = require('glob')

// read definition of specific dependency files we want to copy to webapp
var vendor = require("./vendor.json");

function collect(arr) {
    var paths=[];
    for (var i = 0; i < arr.length; i++) {
        var obj = arr[i];
        paths=paths.concat(glob.sync(obj));
    }
    return paths;
}

var finalName="cplat";

// VENDOR BUILD
gulp.task('process:dependencies', function() {
    ["js", "css", "fonts"].forEach(function(type) {
        var dependencies = vendor[type];
        var paths=collect(dependencies);
        return gulp.src(paths)
            .pipe(expect(dependencies))
            .pipe(gulp.dest("build/gulp/"+type));
    });
});

gulp.task('default', ['process:dependencies']);

gulp.task('watch', function () {
    gulp.watch("vendor.json", ['process:dependencies']);
});