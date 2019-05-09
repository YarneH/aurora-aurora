package com.aurora.aurora;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.aurora.plugin.Plugin;

import java.util.List;


public class PluginAdapter extends ArrayAdapter<Plugin>{

    // View lookup cache
    private static class ViewHolder {
        TextView pluginName;
        TextView pluginDescription;
        ImageView pluginLogo;
    }

    public PluginAdapter(List<Plugin> plugins, Context context) {
        super(context, R.layout.plugin_alert_dialog_adapter_row, plugins);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Plugin plugin = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.plugin_alert_dialog_adapter_row, parent, false);
            viewHolder.pluginName = (TextView) convertView.findViewById(R.id.pluginChooserDialogItemNameTextView);
            viewHolder.pluginDescription = (TextView) convertView.findViewById(R.id.pluginChooserDialogItemDescriptionTextView);
            viewHolder.pluginLogo = (ImageView) convertView.findViewById(R.id.pluginChooserDialogItemLogoItemImageView);

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        viewHolder.pluginName.setText(plugin.getName());
        viewHolder.pluginDescription.setText(plugin.getDescription());
        if (plugin.getPluginLogo() != null) {
            viewHolder.pluginLogo.setImageBitmap(plugin.getPluginLogo());
        }
        // Return the completed view to render on screen
        return convertView;
    }
}





