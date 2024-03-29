package com.aurora.aurora;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.aurora.plugin.Plugin;

import java.util.List;

/**
 * Adapter to represent Plugins with their name, description and icon in the pluginChooser
 */
public class PluginAdapter extends ArrayAdapter<Plugin>{
    private static final String LOG_TAG = PluginAdapter.class.getSimpleName();

    // ViewHolder for the Adapter
    private static final class ViewHolder {
        private TextView pluginName;
        private TextView pluginDescription;
        private ImageView pluginLogo;

        private ViewHolder(){}
    }

    /**
     * Constructs a PluginAdapter
     *
     * @param plugins  The list of Plugins to be shown
     * @param context  A Context object related to the activity the uses this adapter
     */
    PluginAdapter(List<Plugin> plugins, Context context) {
        super(context, R.layout.plugin_alert_dialog_adapter_row, plugins);
    }


    /**
     * Overridden method from ArrayAdapter.
     *
     * @param position      Position of the item in the adapter
     * @param convertView   View that corresponds to the item
     * @param parent        parent ViewGroup
     * @return the inflated and updated convertView
     */
    @Override
    @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        // Get the data item for this position
        Plugin plugin = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(
                    R.layout.plugin_alert_dialog_adapter_row, parent, false);
            viewHolder.pluginName = convertView.findViewById(
                    R.id.pluginChooserDialogItemNameTextView);
            viewHolder.pluginDescription = convertView.findViewById(
                    R.id.pluginChooserDialogItemDescriptionTextView);
            viewHolder.pluginLogo = convertView.findViewById(
                    R.id.pluginChooserDialogItemLogoItemImageView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (plugin != null) {
            viewHolder.pluginName.setText(plugin.getName());
            viewHolder.pluginDescription.setText(plugin.getDescription());
            if (plugin.getPluginLogo() != null) {
                viewHolder.pluginLogo.setImageBitmap(plugin.getPluginLogo());
            } else{
                // Get the icon via the package of the plugin
                try {
                    Drawable icon = getContext().getPackageManager().getApplicationIcon(
                            plugin.getUniqueName());
                    viewHolder.pluginLogo.setImageDrawable(icon);
                } catch (PackageManager.NameNotFoundException e) {
                    Log.e(LOG_TAG, "Package not found when getting icon for packageName: " +
                            plugin.getUniqueName(), e);
                }
            }
        }
        // Return the completed view to render on screen
        return convertView;
    }
}
