package com.ayush.framework.pages;

import com.microsoft.playwright.Page;

/** Page object for checks that apply to whatever page is currently open (headings, URL, header). */
public class GenericPage extends BasePage {

    public GenericPage(Page page) {
        super(page);
    }
}
