package ch.supsi.fscli.model;

import ch.supsi.fscli.application.help.HelpApp;

public class HelpModel implements IHelpModel {

    private static HelpModel myself;

    private HelpApp helpApp = HelpApp.getInstance();

    private HelpModel() {}

    public static HelpModel getInstance() {
        if (myself == null) {
            myself = new HelpModel();
        }
        return myself;
    }

    public String getHelp() {
        return helpApp.getHelp();
    }
}
