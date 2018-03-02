package make.up.the.tool.applicationlauncher;

import android.support.v4.app.Fragment;

/**
 * @author Anatolii Nosenko
 * @version 02 March 2018
 */

public class AppLauncherActivity extends SingleFragmentActivity {

    @Override
    protected Fragment buildFragment() {
        return AppLauncherFragment.newInstance();
    }
}
