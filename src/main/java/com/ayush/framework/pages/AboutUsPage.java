package com.ayush.framework.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class AboutUsPage extends BasePage {

    public static final String PATH = "/pages/about-us";

    public AboutUsPage(Page page) {
        super(page);
    }

    public Locator heading() {
        return mainHeading("About Us");
    }
}
