package com.aurora.kernel;

import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

public class KernelTest {

    private static Kernel mKernel = new Kernel();

    @Test
    public void Kernel_AllObjectsShouldBeMade() {
        // Assert that all communicators have been created
        assertNotNull(mKernel.getAuroraCommunicator());
        assertNotNull(mKernel.getAuroraInternalServiceCommunicator());
        assertNotNull(mKernel.getPluginCommunicator());
        assertNotNull(mKernel.getPluginInternalServiceCommunicator());
        assertNotNull(mKernel.getProcessingCommunicator());
    }

    @Test
    public void Kernel_AllBusInstancesShouldBeEqual() {
        // Assert that all bus instances are identical
        assertEquals(mKernel.getAuroraCommunicator().mBus, mKernel.getAuroraInternalServiceCommunicator().mBus);
        assertEquals(mKernel.getAuroraInternalServiceCommunicator().mBus, mKernel.getPluginCommunicator().mBus);
        assertEquals(mKernel.getPluginCommunicator().mBus, mKernel.getPluginInternalServiceCommunicator().mBus);
        assertEquals(mKernel.getPluginInternalServiceCommunicator().mBus, mKernel.getProcessingCommunicator().mBus);
    }
}
