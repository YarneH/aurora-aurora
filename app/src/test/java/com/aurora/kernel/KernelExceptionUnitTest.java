package com.aurora.kernel;

import org.junit.Test;

/**
 * This is an extra unit test to test exceptions.
 * <p>
 * This is necessary because Kernel is a singleton class, and global state is evil.
 * The reason why we still use a singleton class is further explained
 * <a href="https://github.ugent.be/Aurora/aurora/wiki/kernel-structure#kernel-class">here</a>
 */
public class KernelExceptionUnitTest {

    @Test(expected = ContextNullException.class)
    public void Kernel_getInstance_shouldThrowExceptionWhenContextNull() throws ContextNullException {
        Kernel.getInstance();
    }
}
