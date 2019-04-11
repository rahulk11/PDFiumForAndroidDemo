package cn.xiaolong.pdfiumpdfviewer.pdf.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;

import java.util.concurrent.ConcurrentHashMap;

import cn.xiaolong.pdfiumpdfviewer.R;
import cn.xiaolong.pdfiumpdfviewer.pdf.PDFManager;
import cn.xiaolong.pdfiumpdfviewer.pdf.list.ViewHolder;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;

public class CustomAdapter extends RecyclerView.Adapter<ViewHolder>  {
    private PDFManager mPdfManager;
    private Context mContext;
    private ConcurrentHashMap<Integer, Bitmap> mConcurrentHashMap;
    private View.OnClickListener mOnItemClickListener;
    private int[] mStates;
    private int count;

    public CustomAdapter(Context context, PDFManager pdfManager) {
        this.mPdfManager = pdfManager;
        this.mContext = context;
        mConcurrentHashMap = new ConcurrentHashMap();
        count = mPdfManager.pageCount();
        mStates = new int[count];
    }



    public void setOnItemClickListener(View.OnClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    @Override
    public int getItemCount() {
        return mPdfManager.pageCount();
    }





    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

//        TextView tvGuideName = ((ViewHolder)holder).getTextView();
       // final ImageView imageView = ((ViewHolder)holder).getImageView();
        getBitmap(position).observeOn(AndroidSchedulers.mainThread()).subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Bitmap>() {
                    @Override
                    public void onStart() {
                        super.onStart();
//                        imageView.setImageResource(R.drawable.ic_delete);
                    }

                    @Override
                    public void onCompleted() {
//                Log.d("adapterloadCompleted","completed");

                    }

                    @Override
                    public void onError(Throwable e) {


                        //Log.d("adapterError",e.getMessage());
                    }

                    @Override
                    public void onNext(Bitmap bitmap) {
                        holder.imageView.setImageBitmap(bitmap);
                    }
                });
        holder.textView.setText(position + 1 + "");
        if (mStates[position] == 1) {
            holder.imageView.setSelected(true);
        } else {
            holder.imageView.setSelected(false);
        }
//        if (mOnItemClickListener != null) {
//            holder.imageView.setOnClickListener(v -> {
//                v.setTag(position);
//                mOnItemClickListener.onClick(v);
//                holder.getConvertView().setSelected(false);
//                setStatePosition(position);
//            });
//        }
    }


    public void setStatePosition(int position) {

        mStates = new int[count];
        mStates[position] = 1;
        notifyDataSetChanged();
    }


    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public Observable<Bitmap> getBitmap(final int position) {
        return mConcurrentHashMap.get(position) == null ?
                mPdfManager.getPdfBitmapCustomSize(position, 160)
                        .doOnNext(bitmap -> mConcurrentHashMap.put(position, bitmap)) : Observable.just(mConcurrentHashMap.get(position));
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_guide, null);
        if (mOnItemClickListener != null) {
            layoutView.setOnClickListener(v -> {
                v.setTag(viewType);
                mOnItemClickListener.onClick(v);
                layoutView.setSelected(false);
                setStatePosition(viewType);
            });
        }
        ViewHolder rcv = new ViewHolder(layoutView);
        return rcv;
    }


}
