package com.scbpfsdgis.atcct.Utils;

import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Checkable;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SearchableAdapter extends SimpleAdapter implements Filterable {

    private ArrayList<HashMap<String, String>> originalData = null;
    private ArrayList<HashMap<String, String>> filteredData = null;
    private LayoutInflater mInflater;
    private ItemFilter mFilter = new ItemFilter();

    private int mResource;
    private int mDropDownResource;
    private int[] mTo;
    private String[] mFrom;
    private ViewBinder mViewBinder;
    private LayoutInflater mDropDownInflater;


    public SearchableAdapter(Context context, ArrayList<HashMap<String, String>> data, int resource, String[] from, int[] to) {
        super(context, data, resource, from, to);
        this.filteredData = data;
        this.originalData = data;
        mFrom = from;
        mTo = to;
        mInflater = LayoutInflater.from(context);
        mResource = mDropDownResource = resource;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void setDropDownViewTheme(Resources.Theme theme) {
        if (theme == null) {
            mDropDownInflater = null;
        } else if (theme == mInflater.getContext().getTheme()) {
            mDropDownInflater = mInflater;
        } else {
            final Context context = new ContextThemeWrapper(mInflater.getContext(), theme);
            mDropDownInflater = LayoutInflater.from(context);
        }
    }


    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        final LayoutInflater inflater = mDropDownInflater == null ? mInflater : mDropDownInflater;
        return createViewFromResource(inflater, position, convertView, parent, mDropDownResource);
    }

    public void setDropDownViewResource(int resource) {
        mDropDownResource = resource;
    }

    public int getCount() {
        return filteredData.size();
    }

    public Object getItem(int position) {
        return filteredData.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        return createViewFromResource(mInflater, position, convertView, parent, mResource);
    }

    private View createViewFromResource(LayoutInflater inflater, int position, View convertView,
                                        ViewGroup parent, int resource) {
        View v;
        if (convertView == null) {
            v = inflater.inflate(resource, parent, false);
        } else {
            v = convertView;
        }

        bindView(position, v);

        return v;
    }

    private void bindView(int position, View view) {
        final Map dataSet = filteredData.get(position);
        if (dataSet == null) {
            return;
        }

        final ViewBinder binder = mViewBinder;
        final String[] from = mFrom;
        final int[] to = mTo;
        final int count = to.length;

        for (int i = 0; i < count; i++) {
            final View v = view.findViewById(to[i]);
            if (v != null) {
                final Object data = dataSet.get(from[i]);
                String text = data == null ? "" : data.toString();
                if (text == null) {
                    text = "";
                }

                boolean bound = false;
                if (binder != null) {
                    bound = binder.setViewValue(v, data, text);
                }

                if (!bound) {
                    if (v instanceof Checkable) {
                        if (data instanceof Boolean) {
                            ((Checkable) v).setChecked((Boolean) data);
                        } else if (v instanceof TextView) {
                            // Note: keep the instanceof TextView check at the bottom of these
                            // ifs since a lot of views are TextViews (e.g. CheckBoxes).
                            setViewText((TextView) v, text);
                        } else {
                            throw new IllegalStateException(v.getClass().getName() +
                                    " should be bound to a Boolean, not a " +
                                    (data == null ? "<unknown type>" : data.getClass()));
                        }
                    } else if (v instanceof TextView) {
                        setViewText((TextView) v, text);
                    } else if (v instanceof ImageView) {
                        if (data instanceof Integer) {
                            setViewImage((ImageView) v, (Integer) data);
                        } else {
                            setViewImage((ImageView) v, text);
                        }
                    } else {
                        throw new IllegalStateException(v.getClass().getName() + " is not a " +
                                " view that can be bounds by this SimpleAdapter");
                    }
                }
            }
        }
    }

    static class ViewHolder {
        TextView text;
    }

    public Filter getFilter() {
        return mFilter;
    }

    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            String filterString = constraint.toString().toLowerCase();

            FilterResults results = new FilterResults();

            final ArrayList<HashMap<String, String>> list = originalData;
            System.out.println("Key: " + list.get(0).containsKey("farmName"));

            int count = list.size();
            System.out.println("Original data: " + count);
            final ArrayList<HashMap<String, String>> nlist = new ArrayList<>(count);

            for (int i = 0; i < count; i++) {
                if (list.get(0).containsKey("farmName")) {
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("farmCode", list.get(i).get("farmCode"));
                    hashMap.put("farmName", list.get(i).get("farmName"));
                    hashMap.put("planter", list.get(i).get("planter"));
                    if (list.get(i).get("farmName").toLowerCase().contains(filterString) ||
                            list.get(i).get("planter").toLowerCase().contains(filterString)) {
                        nlist.add(hashMap);
                    }
                } else if (list.get(0).containsKey("farmNameFIR")) {
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("ID", list.get(i).get("ID"));
                    hashMap.put("farmNameFIR", list.get(i).get("farmNameFIR"));
                    hashMap.put("IDFldNo", list.get(i).get("IDFldNo"));
                    if (list.get(i).get("farmNameFIR").toLowerCase().contains(filterString)) {
                        nlist.add(hashMap);
                    }
                } else if (list.get(0).containsKey(("OwnerName"))) {
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("ATCCNo", list.get(i).get("ATCCNo"));
                    hashMap.put("OwnerID", list.get(i).get("OwnerID"));
                    hashMap.put("OwnerName", list.get(i).get("OwnerName"));
                    hashMap.put("ATCCTDetails", list.get(i).get("ATCCTDetails"));
                    hashMap.put("DateSigned", list.get(i).get("DateSigned"));
                    if (list.get(i).get("OwnerName").toLowerCase().contains(filterString)) {
                        nlist.add(hashMap);
                    }
                } else {
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("ownerID", list.get(i).get("ownerID"));
                    hashMap.put("ownerName", list.get(i).get("ownerName"));
                    hashMap.put("ownerMobile", list.get(i).get("ownerMobile"));
                    if (list.get(i).get("ownerName").toLowerCase().contains(filterString)) {
                        nlist.add(hashMap);
                    }
                }

            }
            System.out.println("Filtered data: " + nlist.size());

            results.values = nlist;
            results.count = nlist.size();

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredData = (ArrayList<HashMap<String, String>>) results.values;
            System.out.println("filter: " + results.count);

            notifyDataSetChanged();
        }

    }
}