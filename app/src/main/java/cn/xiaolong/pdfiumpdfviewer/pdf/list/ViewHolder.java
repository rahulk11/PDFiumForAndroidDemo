package cn.xiaolong.pdfiumpdfviewer.pdf.list;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import cn.xiaolong.pdfiumpdfviewer.R;

public class ViewHolder extends RecyclerView.ViewHolder
{
    private final SparseArray<View> mViews;
    private View mConvertView;
    public ImageView imageView;
    public TextView textView;

    public ViewHolder(View itemView) {
        super(itemView);
        this.mViews = new SparseArray<View>();
        this.imageView= itemView.findViewById(R.id.imageView);
        this.textView=itemView.findViewById(R.id.tvLocate);


    }



//    public ViewHolder(Context context, ViewGroup parent, int layoutId,
//                      int position)
//    {
//        this.mViews = new SparseArray<View>();
//        mConvertView = LayoutInflater.from(context).inflate(layoutId, parent,
//                false);
//        //setTag
//        mConvertView.setTag(this);
//    }

    /**
     * 拿到一个ViewHolder对象
     * @param context
     * @param convertView
     * @param parent
     * @param layoutId
     * @param position
     * @return
     */



    /**
     * 通过控件的Id获取对于的控件，如果没有则加入views
     * @param viewId
     * @return
     */
    public <T extends View> T getView(int viewId)
    {

        View view = mViews.get(viewId);
        if (view == null)
        {
            view = mConvertView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }

    public View getConvertView()
    {
        return mConvertView;
    }


    public ImageView getImageView() {
        return imageView;
    }

    public void setImageView(ImageView imageView) {
        this.imageView = imageView;
    }

    public TextView getTextView() {
        return textView;
    }

    public void setTextView(TextView textView) {
        this.textView = textView;
    }
}

