package approval.com.approval_sdi;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import reportku.com.id.R;

public class slide extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener, ViewPager.OnPageChangeListener {
//public class slide  {

    private ViewPager viewPager;
    private RadioGroup group;
    ImageSliderAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.slider);

        viewPager = (ViewPager) findViewById(R.id.image_slider);
        group = (RadioGroup) findViewById(R.id.slider_indicator_group);
        final RadioButton[] rb = new RadioButton[5];

        for(int i=0; i<3; i++){

            rb[i]  = new RadioButton(this);
            if(i == 0){
                rb[i].setChecked(true);
            }
            rb[i].setText(" ");
            rb[i].setId(i + 100);
            group.addView(rb[i]);
        }

        group.setOnCheckedChangeListener(this);
        viewPager.addOnPageChangeListener(this);

        adapter = new ImageSliderAdapter(getSupportFragmentManager());
        adapter.addFragment(ImageSliderFragment.newInstance("http://www.clipartkid.com/images/12/playground-clipart-9sdTKA-clipart.png"));
        adapter.addFragment(ImageSliderFragment.newInstance("http://www.clipartkid.com/images/12/slide-clipart-clipart-panda-free-clipart-images-UzpAUg-clipart.jpg"));
        adapter.addFragment(ImageSliderFragment.newInstance("https://trishdeitch.files.wordpress.com/2010/06/all_tp_products_tp_crazywavy_slide_body_green1.jpg"));

        viewPager.setAdapter(adapter);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        // when current page change -> update radio button state

        int radioButtonId = group.getChildAt(position).getId();
        group.check(radioButtonId);
        Log.v("urutan",String.valueOf(position));
        //
        // Toast.makeText(slide.this,String.valueOf(position),Toast.LENGTH_LONG).show();
    }



    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
        // when checked radio button -> update current page
        viewPager.setCurrentItem(group.indexOfChild(group.findViewById(checkedId)), true);
    }
}
