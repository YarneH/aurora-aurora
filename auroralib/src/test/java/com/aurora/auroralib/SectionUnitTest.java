package com.aurora.auroralib;

import org.junit.Assert;
import org.junit.Test;

public class SectionUnitTest {

    @Test
    public void Section_copyConstructor_shouldReturnCopy() {
        Section section = new Section("sectionContent");
        section.setTitle("sectionTitle");

        Section copy = new Section(section);

        Assert.assertEquals(section, copy);
    }
}
