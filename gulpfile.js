var gulp = require('gulp'),
    serve = require('gulp-serve'),
    mavenTask = require('gulp-maven-integration'),
    child = require('child_process'),
    path = require('path');

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

    }
  },
  artifacts: {
    backendJar: 'k8console-1.0-SNAPSHOT.jar'
  }
};

gulp.task('serve-frontend', serve(conf.paths.src.frontend));
gulp.task('build-backend', mavenTask('Build backend', 'install'));

/**
 * Backend arguments used for development mode.
 *
 * @type {!Array<string>}
 */
const backendDevArgs = [
  '-jar',
  path.join(conf.paths.build.backend, conf.artifacts.backendJar),
  'server',
  'config.yml'
];

/**
 * Currently running backend process object. Null if the backend is not running.
 *
 * @type {?child.ChildProcess}
 */
let runningBackendProcess = null;

/**
 * Kills running backend process (if any).
 */
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

  runningBackendProcess.on('exit', function() {
    // Mark that there is no backend process running anymore.
    runningBackendProcess = null;
  });
});

gulp.task('serve', [ 'serve-backend', 'serve-frontend' ]);