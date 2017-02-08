package com.tunaemre.opencv.faceswap;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;
import com.tunaemre.opencv.faceswap.R;
import com.tunaemre.opencv.faceswap.view.ExtendedActivity;

public class MainActivity extends ExtendedActivity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	protected void prepareActivity()
	{
		findViewById(R.id.btnMainFaceSwap).setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				startActivity(new Intent(MainActivity.this, FaceSwapActivity.class));
			}
		});
		
		findViewById(R.id.btnMainFaceMask).setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Toast.makeText(MainActivity.this, "Under developing.", Toast.LENGTH_SHORT).show();
			}
		});
	}
}