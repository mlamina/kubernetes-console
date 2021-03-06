let fs;
try {
  fs = require('graceful-fs')
} catch (_) {
  fs = require('fs')
}
let gulp = require('gulp'),
  serve = require('gulp-serve'),
  child = require('child_process'),
  exec = require('child_process').exec,
  path = require('path'),
  watch = require('gulp-watch'),
  sass = require('gulp-sass'),
  jsonfile = require('jsonfile');

const appConfig = {
  dev: {
    backendUrl: 'http://localhost:8082/api'
  },
  production: {
    backendUrl: '/api'
  }
};

const conf = {
  paths: {
    src: {
      frontend: 'src/frontend',
      backend: 'src/backend'
    },
    build: {
      frontend: 'src/backend/main/resources',
      backend: 'target'
    },
    artifacts: {

    },
    config: 'src/frontend/js/config.js'
  },
  artifacts: {
    backendJar: 'k8console-1.0-SNAPSHOT.jar'
  }
};

function writeConfig(config) {
  let content = 'module.exports = ' + JSON.stringify(config) + ';\n';
  fs.writeFile(conf.paths.config, content, (err) => console.log);
}

gulp.task('write-dev-config', function() { writeConfig(appConfig.dev); });
gulp.task('write-prod-config', function() { writeConfig(appConfig.production); });

gulp.task('styles', function() {
  gulp.src(path.join(conf.paths.src.frontend, 'sass/style.scss'))
    .pipe(sass().on('error', sass.logError))
    .pipe(gulp.dest(path.join(conf.paths.src.frontend, 'css')));
});


gulp.task('serve-frontend', ['write-dev-config', 'styles'], serve(conf.paths.src.frontend));
gulp.task('build-backend', function (cb) {
  exec('mvn package -Dmaven.javadoc.skip=true -DskipTests=true', function (err, stdout, stderr) {
    // console.log(stdout);
    console.log(stderr);
    cb(err);
  });
});


/**
 * Currently running backend process object. Null if the backend is not running.
 *
 * @type {?child.ChildProcess}
 */
let runningBackendProcess = null;
let backendDevArgs = [
  '-jar',
  path.join(conf.paths.build.backend, conf.artifacts.backendJar),
  'server',
  'config.yml'
];

gulp.task('kill-backend', function(doneFn) {
  if (runningBackendProcess) {
    runningBackendProcess.on('exit', function() {
      // Mark that there is no backend process running anymore.
      runningBackendProcess = null;
      // Finish the task only when the backend is actually killed.
      doneFn();
    });
    runningBackendProcess.kill();
  } else {
    doneFn();
  }
});

/**
 * Spawns new backend application process and finishes the task immediately. Previously spawned
 * backend process is killed beforehand, if any.
 */
gulp.task('serve-backend', ['build-backend', 'kill-backend'], function() {
  runningBackendProcess = child.spawn(
    'java', backendDevArgs,
    {stdio: 'inherit', cwd: __dirname});
  // {cwd: __dirname});

  runningBackendProcess.on('exit', function() {
    // Mark that there is no backend process running anymore.
    runningBackendProcess = null;
  });
});

gulp.task('serve', [ 'serve-backend', 'serve-frontend' ], function () {
  gulp.watch(path.join(conf.paths.src.frontend, 'sass/**/*.scss'), ['styles']);
  gulp.watch(path.join(conf.paths.src.backend, 'main/**/*.java'), ['serve-backend']);
  gulp.watch('gulpfile.js', ['write-dev-config']);
});