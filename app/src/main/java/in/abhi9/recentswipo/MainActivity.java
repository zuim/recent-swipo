package in.abhi9.recentswipo;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.CheckBox;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

	    final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
	    final Intent intent = new Intent(this, AppLogService.class);

		CheckBox loop = findViewById(R.id.loop);
		loop.setChecked(prefs.getBoolean("loop", false));
		loop.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				prefs.edit().putBoolean("loop", ((CheckBox) view).isChecked()).apply();
				stopService(intent);
				startService(intent);
			}
		});

        // Starting service
        if (AppLogService.getServiceObject() == null) {
            startService(intent);
        }
    }
}
