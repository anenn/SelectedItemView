package com.anenn.selecteditemview;

import android.content.Context;
import android.util.TypedValue;

/**
 * Created by Anenn on 9/5/16.
 *
 * 尺寸转换工具
 */
public class DimenUtil {

  public static float dp2sp(Context context, int value) {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value,
        context.getResources().getDisplayMetrics());
  }
}
