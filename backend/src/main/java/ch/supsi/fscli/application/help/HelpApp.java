package ch.supsi.fscli.application.help;

import ch.supsi.fscli.business.help.HelpLogic;

public class HelpApp implements IHelpApp {
    private static HelpApp myself;

    public static HelpApp getInstance() {
        if (myself == null) {
            myself = new HelpApp();
        }
        return myself;
    }

    @Override
    public String getHelp() {
        return HelpLogic.getInstance().getHelp();
    }
}