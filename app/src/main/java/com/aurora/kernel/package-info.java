/**
 * This package contains all classes related to the kernel, the beating heart of the application that
 * takes care of communication between all the other parts of the application.
 *
 * <p>
 * At the center of the kernel , the {@link com.aurora.kernel.Bus} can be found.
 * The bus is responsible for passing events between the various communicators.
 * It is a fully functioning event system that works with Observables from RxJava.
 * For more information on RxJava and how Observables work,
 * visit <a href="https://github.com/ReactiveX/RxJava">this link</a>.
 * </p>
 *
 * <p>
 * Each of the communicators is responsible for communicating with one other part of the application.
 * This way, the other parts of the application are abstracted away for everyone, with the kernel
 * functioning as a decoupling link.
 * </p>
 *
 * <p>
 * Finally, the {@link com.aurora.kernel.PluginRegistry} is responsible for registering plugins
 * along with metadata about that plugin.
 * </p>
 */
package com.aurora.kernel;