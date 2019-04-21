
package cn.xiaolong.pdfiumpdfviewer;


import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.RecyclerView;

import java.io.File;

import cn.xiaolong.pdfiumpdfviewer.download.DownLoadInfo;
import cn.xiaolong.pdfiumpdfviewer.download.DownLoadManager;
import cn.xiaolong.pdfiumpdfviewer.download.HttpProgressOnNextListener;
import cn.xiaolong.pdfiumpdfviewer.pdf.CircleProgressBar;
import cn.xiaolong.pdfiumpdfviewer.pdf.PDFManager;
import cn.xiaolong.pdfiumpdfviewer.pdf.adapter.CustomAdapter;
import cn.xiaolong.pdfiumpdfviewer.pdf.adapter.PdfGuideAdapter;
import cn.xiaolong.pdfiumpdfviewer.pdf.adapter.PdfImageAdapter;
import cn.xiaolong.pdfiumpdfviewer.pdf.utils.FileUtils;

public class MainActivity extends Activity implements HttpProgressOnNextListener<DownLoadInfo> {

    private CircleProgressBar cpbLoad;
    private PDFManager mPDFManager;
    private ViewPager mViewpager;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager linearLayoutManager;
    private ImageView vGuide;
    private PdfGuideAdapter pdfGuideAdapter;
    private CustomAdapter customAdapter;
    private File downLoadPdfFile;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        linearLayoutManager = new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.HORIZONTAL,false);
        setContentView(R.layout.activity_main);
        init();
        setListener();

    }


    protected void init() {
        mRecyclerView=(RecyclerView)findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        cpbLoad = (CircleProgressBar) findViewById(R.id.cpbLoad);
        vGuide = findViewById(R.id.vGuide);
        vGuide.setVisibility(View.GONE);
        initcpbConfig();

        downLoadPdfFile = new File(this.getCacheDir(), "lucky" + ".pdf");
        if (downLoadPdfFile.exists() && FileUtils.getFileSize(downLoadPdfFile) > 0) {
            loadFinish();
        } else {
            DownLoadManager.getDownLoadManager().startDown(getDownLoadInfo());
        }
    }

    private void initcpbConfig() {
        cpbLoad.setValue(0);
        cpbLoad.setTextColor(getResources().getColor(R.color.main_normal_black_color));
        cpbLoad.setProdressWidth(cpbLoad.dp2px(8));
        cpbLoad.setProgress(getResources().getColor(R.color.main_blue_color));
        cpbLoad.setCircleBackgroud(Color.WHITE);
        cpbLoad.setPreProgress(Color.WHITE);
    }

    private void loadFinish() {
        mPDFManager = new PDFManager.Builder(this)
                .pdfFromFile(downLoadPdfFile)
                .setOnOpenErrorListener(t -> {
                    cpbLoad.setValue(0);
                    DownLoadManager.getDownLoadManager().startDown(getDownLoadInfo());
                })
                .setOnOpenSuccessListener(() -> {
                    cpbLoad.setVisibility(View.GONE);
                    vGuide.setVisibility(View.VISIBLE);
                })
                .build();
        vGuide.setVisibility(View.VISIBLE);
        initListView();
        initViewPager();
    }

    private void initViewPager() {
        mViewpager = (ViewPager) findViewById(R.id.viewpager);
        mViewpager.setAdapter(new PdfImageAdapter(this, mPDFManager));

        final TextView textView = (TextView) findViewById(R.id.tvPosition);
        textView.setVisibility(View.VISIBLE);
        textView.setText(1 + "/" + mPDFManager.pageCount());
        mViewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }
            @Override
            public void onPageSelected(int position) {
                textView.setText(position + 1 + "/" + mPDFManager.pageCount());
//                mRecyclerView.getAdapter().
                customAdapter.setStatePosition(position);
                mRecyclerView.smoothScrollToPosition(position);

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void initListView() {
       mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
      mRecyclerView.setVisibility(View.GONE);
         customAdapter = new CustomAdapter(this, mPDFManager);
        customAdapter.setOnItemClickListener(v -> mViewpager.setCurrentItem((int) v.getTag()));
        mRecyclerView.setAdapter(customAdapter);
    }

    private DownLoadInfo getDownLoadInfo() {
        DownLoadInfo mDownLoadInfo = new DownLoadInfo();
          /*下载回调*/
        mDownLoadInfo.listener = this;
        mDownLoadInfo.savePath = downLoadPdfFile.getAbsolutePath();
        mDownLoadInfo.url = "https://archive.org/download/theoryoffilmrede00krac/theoryoffilmrede00krac.pdf";
        return mDownLoadInfo;
    }

    protected void setListener() {
        vGuide.setOnClickListener(v -> {
            if (!vGuide.isSelected()) {
//                RelativeLayout.LayoutParams layoutParams= (RelativeLayout.LayoutParams) vGuide.getLayoutParams();
//                layoutParams.addRule(RelativeLayout.ALIGN_TOP,R.id.recycler_view);
//                RelativeLayout.LayoutParams params=new RelativeLayout.LayoutParams(100,100);
//                params.addRule(RelativeLayout.ALIGN_TOP,R.id.recycler_view);
//                params.addRule(RelativeLayout.CENTER_HORIZONTAL);
//                vGuide.setLayoutParams(params);
                mRecyclerView.setVisibility(View.VISIBLE);
                vGuide.setSelected(true);
            } else {
//                RelativeLayout.LayoutParams params=new RelativeLayout.LayoutParams(100,100);
//                params.addRule(RelativeLayout.CENTER_HORIZONTAL);
//                params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
//                vGuide.setLayoutParams(params);
               mRecyclerView.setVisibility(View.GONE);
                vGuide.setSelected(false);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DownLoadManager.getDownLoadManager().stopAllDown();
        if (mPDFManager != null) {
            mPDFManager.recycle();
        }
    }


    @Override
    public void onNext(DownLoadInfo downLoadInfo) {
        Toast.makeText(this, downLoadInfo.savePath, Toast.LENGTH_LONG).show();
        loadFinish();
    }

    @Override
    public void onLoadStart() {
        cpbLoad.setValue(0);
        cpbLoad.setVisibility(View.VISIBLE);
    }

    @Override
    public void onLoadComplete() {
        cpbLoad.setVisibility(View.GONE);
    }

    @Override
    public void updateProgress(long readLength, long countLength) {
        cpbLoad.setValue((int) (readLength * 100 / countLength));
    }

    @Override
    public void onLoadError(Throwable e) {
        e.printStackTrace();
        Toast.makeText(this, "下载发生异常错误。", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onLoadPause() {
        Toast.makeText(this, "暂停下载", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onLoadStop() {
        Toast.makeText(this, "停止下载", Toast.LENGTH_LONG).show();
    }

}
