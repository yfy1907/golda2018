package com.example.administrator.goldaappnew.utils;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.example.administrator.goldaappnew.R;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;


public class DrawableTool {
	public static HashMap map = new HashMap();

	public static int getValue(String icon) {
		if (map.size() == 0) {
			map.put("blue_big_all_stand", R.drawable.blue_big_all_stand);
			map.put("blue_big_bridge",R.drawable.blue_big_bridge);
			map.put("blue_big_building",R.drawable.blue_big_building);
			map.put("blue_big_car",R.drawable.blue_big_car);
			map.put("blue_big_fence",R.drawable.blue_big_fence);
			map.put("blue_big_led",R.drawable.blue_big_led);
			map.put("blue_big_led_board",R.drawable.blue_big_led_board);
			map.put("blue_big_neon",R.drawable.blue_big_neon);
			map.put("blue_big_single_board",R.drawable.blue_big_single_board);
			map.put("blue_big_three_face",R.drawable.blue_big_three_face);
			map.put("blue_big_three_stand",R.drawable.blue_big_three_stand);
			map.put("blue_big_two_stand",R.drawable.blue_big_two_stand);
			map.put("blue_big_wall",R.drawable.blue_big_wall);
			map.put("blue_big_wall_paint",R.drawable.blue_big_wall_paint);
			map.put("blue_small_alien_lamp",R.drawable.blue_small_alien_lamp);
			map.put("blue_small_balloon",R.drawable.blue_small_balloon);
			map.put("blue_small_banner",R.drawable.blue_small_banner);
			map.put("blue_small_direction",R.drawable.blue_small_direction);
			map.put("blue_small_door",R.drawable.blue_small_door);
			map.put("blue_small_falg",R.drawable.blue_small_falg);
			map.put("blue_small_gas",R.drawable.blue_small_gas);
			map.put("blue_small_land_paint",R.drawable.blue_small_land_paint);
			map.put("blue_small_led",R.drawable.blue_small_led);
			map.put("blue_small_neon",R.drawable.blue_small_neon);
			map.put("blue_small_pager",R.drawable.blue_small_pager);
			map.put("blue_small_phone",R.drawable.blue_small_phone);
			map.put("blue_small_poster",R.drawable.blue_small_poster);
			map.put("blue_small_shadow",R.drawable.blue_small_shadow);
			map.put("blue_small_showwindow",R.drawable.blue_small_showwindow);
			map.put("blue_small_single",R.drawable.blue_small_single);
			map.put("blue_small_waittingroom",R.drawable.blue_small_waittingroom);
			map.put("blue_small_dusbin",R.drawable.blue_small_dusbin);						//last add
			map.put("pink_big_all_stand",R.drawable.pink_big_all_stand);
			map.put("pink_big_bridge",R.drawable.pink_big_bridge);
			map.put("pink_big_building",R.drawable.pink_big_building);
			map.put("pink_big_car",R.drawable.pink_big_car);
			map.put("pink_big_fence",R.drawable.pink_big_fence);
			map.put("pink_big_led",R.drawable.pink_big_led);
			map.put("pink_big_led_board",R.drawable.pink_big_led_board);
			map.put("pink_big_neon",R.drawable.pink_big_neon);
			map.put("pink_big_single_board",R.drawable.pink_big_single_board);
			map.put("pink_big_three_face",R.drawable.pink_big_three_face);
			map.put("pink_big_three_stand",R.drawable.pink_big_three_stand);
			map.put("pink_big_two_stand",R.drawable.pink_big_two_stand);
			map.put("pink_big_wall",R.drawable.pink_big_wall);
			map.put("pink_big_wall_paint",R.drawable.pink_big_wall_paint);
			map.put("pink_small_alien_lamp",R.drawable.pink_small_alien_lamp);
			map.put("pink_small_balloon",R.drawable.pink_small_balloon);
			map.put("pink_small_banner",R.drawable.pink_small_banner);
			map.put("pink_small_direction",R.drawable.pink_small_direction);
			map.put("pink_small_door",R.drawable.pink_small_door);
			map.put("pink_small_falg",R.drawable.pink_small_falg);
			map.put("pink_small_gas",R.drawable.pink_small_gas);
			map.put("pink_small_land_paint",R.drawable.pink_small_land_paint);
			map.put("pink_small_led",R.drawable.pink_small_led);
			map.put("pink_small_neon",R.drawable.pink_small_neon);
			map.put("pink_small_pager",R.drawable.pink_small_pager);
			map.put("pink_small_phone",R.drawable.pink_small_phone);
			map.put("pink_small_poster",R.drawable.pink_small_poster);
			map.put("pink_small_shadow",R.drawable.pink_small_shadow);
			map.put("pink_small_showwindow",R.drawable.pink_small_showwindow);
			map.put("pink_small_single",R.drawable.pink_small_single);
			map.put("pink_small_waittingroom",R.drawable.pink_small_waittingroom);
			map.put("pink_small_dusbin",R.drawable.pink_small_dusbin);					//last add
			map.put("error",R.drawable.error);
			map.put("green_big_all_stand",R.drawable.green_big_all_stand);
			map.put("green_big_bridge",R.drawable.green_big_bridge);
			map.put("green_big_building",R.drawable.green_big_building);
			map.put("green_big_car",R.drawable.green_big_car);
			map.put("green_big_fence",R.drawable.green_big_fence);
			map.put("green_big_led",R.drawable.green_big_led);
			map.put("green_big_led_board",R.drawable.green_big_led_board);
			map.put("green_big_neon",R.drawable.green_big_neon);
			map.put("green_big_single_board",R.drawable.green_big_single_board);
			map.put("green_big_three_face",R.drawable.green_big_three_face);
			map.put("green_big_three_stand",R.drawable.green_big_three_stand);
			map.put("green_big_two_stand",R.drawable.green_big_two_stand);
			map.put("green_big_wall",R.drawable.green_big_wall);
			map.put("green_big_wall_paint",R.drawable.green_big_wall_paint);
			map.put("green_small_alien_lamp",R.drawable.green_small_alien_lamp);
			map.put("green_small_balloon",R.drawable.green_small_balloon);
			map.put("green_small_banner",R.drawable.green_small_banner);
			map.put("green_small_direction",R.drawable.green_small_direction);
			map.put("green_small_door",R.drawable.green_small_door);
			map.put("green_small_falg",R.drawable.green_small_falg);
			map.put("green_small_gas",R.drawable.green_small_gas);
			map.put("green_small_land_paint",R.drawable.green_small_land_paint);
			map.put("green_small_led",R.drawable.green_small_led);
			map.put("green_small_neon",R.drawable.green_small_neon);
			map.put("green_small_pager",R.drawable.green_small_pager);
			map.put("green_small_phone",R.drawable.green_small_phone);
			map.put("green_small_poster",R.drawable.green_small_poster);
			map.put("green_small_shadow",R.drawable.green_small_shadow);
			map.put("green_small_showwindow",R.drawable.green_small_showwindow);
			map.put("green_small_single",R.drawable.green_small_single);
			map.put("green_small_waittingroom",R.drawable.green_small_waittingroom);
			map.put("green_small_dusbin",R.drawable.green_small_dusbin);				//last add
			map.put("red_big_all_stand",R.drawable.red_big_all_stand);
			map.put("red_big_bridge",R.drawable.red_big_bridge);
			map.put("red_big_building",R.drawable.red_big_building);
			map.put("red_big_car",R.drawable.red_big_car);
			map.put("red_big_fence",R.drawable.red_big_fence);
			map.put("red_big_led",R.drawable.red_big_led);
			map.put("red_big_led_board",R.drawable.red_big_led_board);
			map.put("red_big_neon",R.drawable.red_big_neon);
			map.put("red_big_single_board",R.drawable.red_big_single_board);
			map.put("red_big_three_face",R.drawable.red_big_three_face);
			map.put("red_big_three_stand",R.drawable.red_big_three_stand);
			map.put("red_big_wall",R.drawable.red_big_wall);
			map.put("red_big_wall_paint",R.drawable.red_big_wall_paint);
			map.put("red_big_two_stand",R.drawable.red_big_two_stand);
			map.put("red_small_alien_lamp",R.drawable.red_small_alien_lamp);
			map.put("red_small_balloon",R.drawable.red_small_balloon);
			map.put("red_small_banner",R.drawable.red_small_banner);
			map.put("red_small_direction",R.drawable.red_small_direction);
			map.put("red_small_door",R.drawable.red_small_door);
			map.put("red_small_falg",R.drawable.red_small_falg);
			map.put("red_small_gas",R.drawable.red_small_gas);
			map.put("red_small_land_paint",R.drawable.red_small_land_paint);
			map.put("red_small_led",R.drawable.red_small_led);
			map.put("red_small_neon",R.drawable.red_small_neon);
			map.put("red_small_pager",R.drawable.red_small_pager);
			map.put("red_small_phone",R.drawable.red_small_phone);
			map.put("red_small_poster",R.drawable.red_small_poster);
			map.put("red_small_shadow",R.drawable.red_small_shadow);
			map.put("red_small_showwindow",R.drawable.red_small_showwindow);
			map.put("red_small_single",R.drawable.red_small_single);
			map.put("red_small_waittingroom",R.drawable.red_small_waittingroom);
			map.put("red_small_dusbin",R.drawable.red_small_dusbin);				//last add
		}
		Integer result = (Integer) map.get(icon);
		if (result != null) {
			return result.intValue();
		}else{
			return 0;
		}
	}

}
