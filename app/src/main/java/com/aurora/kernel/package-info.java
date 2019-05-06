/**
 * This package contains all classes related to the kernel, the beating heart of the application that
 * takes care of communication between all the other parts of the application.
 *
 * <p>
 * The {@link com.aurora.kernel.Kernel} class is a singleton class and acts as the top level component for the kernel.
 * It is responsible for bootstrapping and configuring all the communicators and other components.
 * The class is singleton because different parts of the application need to be able to access it.
 * For example, a Kernel instance may be needed in the activities of the application, as well as in the services that
 * Aurora provides to the plugins.
 * </p>
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
