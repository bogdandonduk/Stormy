package proto.android.stormy.core.extensions

import android.app.Activity
import android.content.Context
import androidx.core.content.res.ResourcesCompat
import bogdandonduk.tooltiptoolboxlib.TooltipBuilder
import bogdandonduk.tooltiptoolboxlib.TooltipToolbox
import proto.android.stormy.R

fun TooltipToolbox.configureTooltipBuilder(context: Context, builder: TooltipBuilder) = builder
    .setTextColor {
        ResourcesCompat.getColor(context.resources, R.color.light, null)
    }
    .setBackgroundColor {
        ResourcesCompat.getColor(context.resources, R.color.dark, null)
    }

fun TooltipToolbox.configureGoBackTooltip(activity: Activity, builder: TooltipBuilder) = builder
    .setTextColor {
        ResourcesCompat.getColor(activity.resources, R.color.light, null)
    }
    .setBackgroundColor {
        ResourcesCompat.getColor(activity.resources, R.color.dark, null)
    }
    .setText {
        activity.getString(R.string.go_back)
    }
    .setOnClickAction { _, _ ->
        activity.onBackPressed()
    }


