package com.iscool.edward.stockmarkettwitter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class AssetAdapter extends RecyclerView.Adapter<AssetAdapter.AssetHolder> {
    ArrayList<Asset> assetArrayList;
    TextView netWorth;
    Context mContext;
    public Double totalCapital=0.0;

    AssetAdapter(ArrayList<Asset> assetArrayList,Context context,TextView netWorth){
        this.assetArrayList=assetArrayList;
        mContext = context;
        this.netWorth = netWorth;
    }

    @Override
    public AssetAdapter.AssetHolder onCreateViewHolder(ViewGroup parent, int viewType){
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View viewHolder = inflater.inflate(R.layout.asset_item_view,parent,false);
        return new AssetHolder(viewHolder);
    }

    public class AssetHolder extends RecyclerView.ViewHolder{
        TextView assetQuantity;
        TextView assetName;
        TextView assetPrice;

        AssetHolder(View v){
            super(v);
            assetQuantity = v.findViewById(R.id.assetQuantity);
            assetName = v.findViewById(R.id.assetName);
            assetPrice = v.findViewById(R.id.assetValue);
        }

        public void bindAsset(Asset asset){
            assetQuantity.setText(Integer.toString(asset.shares) + " shares");
            assetName.setText(asset.company);
            //create thread
            new Thread(new Runnable(){
                public void run(){
                    double price = StockChart.currentPrice(asset.ticker);
                    totalCapital+=Math.round(price*asset.shares);
                    assetPrice.post(new Runnable(){
                        public void run(){
                            assetPrice.setText("$" + Double.toString(price));
                            netWorth.setText("Stock Earnings: $" + totalCapital.toString());
                        }
                    });
                }
            }).start();
        }
    }

    Double getTotalCapital(){
        return totalCapital;
    }

    @Override
    public int getItemCount(){
        return assetArrayList.size();
    }

    @Override
    public void onBindViewHolder(AssetHolder assetHolder,int position){
        Asset a = assetArrayList.get(position);
        assetHolder.bindAsset(a);
    }

}
