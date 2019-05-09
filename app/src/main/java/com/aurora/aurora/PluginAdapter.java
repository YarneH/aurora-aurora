package com.aurora.aurora;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
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

    // ViewHolder for the Adapter
    private static class ViewHolder {
        private TextView pluginName;
        private TextView pluginDescription;
        private ImageView pluginLogo;

        private ViewHolder(){}
    }

    public PluginAdapter(List<Plugin> plugins, Context context) {
        super(context, R.layout.plugin_alert_dialog_adapter_row, plugins);
    }


    /**
     * Overriden method from ArrayAdapter.
     *
     * @param position      Position of the item in the adapter
     * @param convertView   View that corresponds to the item
     * @param parent        parent Viewgroup
     * @return the inflated and updated convertView
     */
    @Override
    @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        // Get the data item for this position
        Plugin plugin = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder;

        final View result;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(
                    R.layout.plugin_alert_dialog_adapter_row, parent, false);
            viewHolder.pluginName = (TextView) convertView.findViewById(
                    R.id.pluginChooserDialogItemNameTextView);
            viewHolder.pluginDescription = (TextView) convertView.findViewById(
                    R.id.pluginChooserDialogItemDescriptionTextView);
            viewHolder.pluginLogo = (ImageView) convertView.findViewById(
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
                    Drawable icon = getContext().getPackageManager().getApplicationIcon(plugin.getUniqueName());
                    viewHolder.pluginLogo.setImageDrawable(icon);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        // Return the completed view to render on screen
        return convertView;
    }

}





