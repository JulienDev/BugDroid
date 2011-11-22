package fr.julienvermet.bugdroid.products;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import fr.julienvermet.bugdroid.R;

public class ProductsListAdapter extends BaseAdapter {

	ArrayList<Product> products = new ArrayList<Product>();
	Context ctx;
	LayoutInflater inflater;
	
	public ProductsListAdapter(ArrayList<Product> products, Context ctx) {
		super();
		this.products = products;
		this.ctx = ctx;
		this.inflater = LayoutInflater.from(ctx);
	}

	@Override
	public int getCount() {
		return products.size();
	}

	@Override
	public Product getItem(int position) {
		return products.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder;

		if(convertView == null) {

			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.product_element, null);
			holder.tvProductName = (TextView)convertView.findViewById(R.id.tvProductName);
			holder.tvProductDescription = (TextView)convertView.findViewById(R.id.tvProductDescription);
			holder.ivProduct = (ImageView)convertView.findViewById(R.id.ivProduct);
			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.tvProductName.setText( getItem(position).getName() );
		holder.tvProductDescription.setText( getItem(position).getDescription() );
		holder.ivProduct.setImageResource( getItem(position).getImage() );

		return convertView;
	}

	private class ViewHolder {
		TextView tvProductName;
		TextView tvProductDescription;
		ImageView ivProduct;
	}
}