package com.epgis.marker;

import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.epgis.commons.geojson.Feature;
import com.epgis.commons.geojson.Point;
import com.epgis.epgisapp.R;
import com.epgis.mapsdk.activities.anim.AnimatedMarkerActivity;
import com.epgis.mapsdk.activities.annotations.MarkerActivity;
import com.epgis.mapsdk.annotations.BaseMarkerOptions;
import com.epgis.mapsdk.annotations.IconFactory;
import com.epgis.mapsdk.annotations.Marker;
import com.epgis.mapsdk.annotations.MarkerOptions;
import com.epgis.mapsdk.camera.CameraPosition;
import com.epgis.mapsdk.geometry.LatLng;
import com.epgis.mapsdk.maps.AegisMap;
import com.epgis.mapsdk.maps.MapView;
import com.epgis.mapsdk.maps.OnMapReadyCallback;
import com.epgis.mapsdk.maps.Style;
import com.epgis.mapsdk.style.layers.PropertyFactory;
import com.epgis.mapsdk.style.layers.SymbolLayer;
import com.epgis.mapsdk.style.sources.GeoJsonSource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author fcy
 * 初始化打点
 * 2020-10-28
 */
public class InitMarkerActivity extends AppCompatActivity implements View.OnClickListener,AegisMap.OnMapClickListener{

    private MapView mapView;    // 地图视图对象
    private AegisMap maegisMap;
    private Marker marker;        // 图标对象
    private boolean isAdded;    // 图标是否添加
    private ValueAnimator animator;
    private GeoJsonSource geoJsonSource;

    //默认相机位置
    private LatLng targetPos = new LatLng(32.074443, 118.803284);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initmanage);
        ImageView goBack = findViewById(R.id.iv_goback);
        goBack.setOnClickListener(this);
        mapView = findViewById(R.id.mapView);    // 地图视图对象获取
        mapView.onCreate(savedInstanceState);   // 用于地图恢复状态使用
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull AegisMap aegisMap) {
                maegisMap = aegisMap;
                geoJsonSource = new GeoJsonSource("source-id",
                        Feature.fromGeometry(Point.fromLngLat(targetPos.getLongitude(),
                                targetPos.getLatitude())));
                // 移图到指定位置
                aegisMap.setCameraPosition(new CameraPosition.Builder().target(targetPos).zoom(12D).build());
                // 设置地图显示样式
                aegisMap.setStyle(Style.STREETS, new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {
                        style.addImage(("marker_icon"), BitmapFactory.decodeResource(
                                getResources(), R.drawable.blue_circle));

                        style.addSource(geoJsonSource);

                        style.addLayer(new SymbolLayer("layer-id", "source-id")
                                .withProperties(
                                        PropertyFactory.iconImage("marker_icon"),
                                        PropertyFactory.iconIgnorePlacement(true),
                                        PropertyFactory.iconAllowOverlap(true)
                                ));
                        maegisMap.addOnMapClickListener(InitMarkerActivity.this);
                    }
                });
            }
        });
    }

    /**
     * 初始化默认的图标
     *
     * @param aegisMap 地图交互对象
     */
    private void initDefaultIconMarker(AegisMap aegisMap) {
        aegisMap.setInfoWindowAdapter(new AegisMap.InfoWindowAdapter() {
            @Nullable
            @Override
            public View getInfoWindow(@NonNull Marker marker) {
                return new Button(InitMarkerActivity.this);
            }
        });

        List<BaseMarkerOptions> markerOptionsList=new ArrayList<>();
        String arrMap="[{\"point_x\":\"119.862616002559500\",\"point_y\":\"34.009479004889750\"},{\"point_x\":\"116.561986006796500\",\"point_y\":\"34.559173002839050\"},{\"point_x\":\"117.345541515002000\",\"point_y\":\"34.201678199105850\"},{\"point_x\":\"119.426270128237500\",\"point_y\":\"34.273606284932800\"},{\"point_x\":\"120.183739006519500\",\"point_y\":\"33.336069002747550\"},{\"point_x\":\"120.736369390332000\",\"point_y\":\"31.003308026877000\"},{\"point_x\":\"120.746650639280000\",\"point_y\":\"31.335276036063800\"},{\"point_x\":\"120.734296735972000\",\"point_y\":\"32.101973693515000\"},{\"point_x\":\"119.877652000636000\",\"point_y\":\"33.221047006547450\"},{\"point_x\":\"118.278270472477500\",\"point_y\":\"33.433930096345850\"},{\"point_x\":\"120.807738006115000\",\"point_y\":\"31.654484000056950\"},{\"point_x\":\"119.109181758252000\",\"point_y\":\"34.367312379298700\"},{\"point_x\":\"120.457715957048000\",\"point_y\":\"32.567418557437250\"},{\"point_x\":\"119.578458616077000\",\"point_y\":\"32.465615183979600\"},{\"point_x\":\"116.533252004534000\",\"point_y\":\"34.711426001042150\"},{\"point_x\":\"116.575847007334500\",\"point_y\":\"34.725956007838250\"},{\"point_x\":\"120.262432706855780\",\"point_y\":\"33.329231357011155\"},{\"point_x\":\"119.574013300000000\",\"point_y\":\"32.465339160000000\"},{\"point_x\":\"120.088737592580000\",\"point_y\":\"32.642729023390550\"},{\"point_x\":\"118.123794462195500\",\"point_y\":\"33.865340260941900\"},{\"point_x\":\"121.046195234157000\",\"point_y\":\"31.527816598653050\"},{\"point_x\":\"121.037722333644500\",\"point_y\":\"31.586719443961550\"},{\"point_x\":\"118.704714630120145\",\"point_y\":\"32.133571569216222\"},{\"point_x\":\"120.732954642009500\",\"point_y\":\"32.076222598970950\"},{\"point_x\":\"119.427087064281500\",\"point_y\":\"32.923377336457200\"},{\"point_x\":\"118.332142157144000\",\"point_y\":\"34.180091370526750\"},{\"point_x\":\"121.024760755898000\",\"point_y\":\"31.372123440275100\"},{\"point_x\":\"121.024718351301000\",\"point_y\":\"31.372119106179300\"},{\"point_x\":\"118.543167002499500\",\"point_y\":\"34.682698000222450\"},{\"point_x\":\"120.801496006548500\",\"point_y\":\"31.769905004650350\"},{\"point_x\":\"120.313358527947500\",\"point_y\":\"31.813470549954000\"},{\"point_x\":\"119.588738005608500\",\"point_y\":\"32.133480999618750\"},{\"point_x\":\"120.900248005986500\",\"point_y\":\"32.226376000791750\"},{\"point_x\":\"117.026547007262500\",\"point_y\":\"34.406338006258000\"},{\"point_x\":\"120.136429000646000\",\"point_y\":\"33.374881006777250\"},{\"point_x\":\"120.164926007390000\",\"point_y\":\"33.366096001118450\"},{\"point_x\":\"120.140738002955500\",\"point_y\":\"33.395760007202650\"},{\"point_x\":\"120.952020967857000\",\"point_y\":\"31.404717867620350\"},{\"point_x\":\"120.133428007364500\",\"point_y\":\"33.372817002236850\"},{\"point_x\":\"119.659612962465000\",\"point_y\":\"32.313060153703350\"},{\"point_x\":\"119.223671224278515\",\"point_y\":\"31.659072822686451\"},{\"point_x\":\"119.393775968291000\",\"point_y\":\"34.302653045273150\"},{\"point_x\":\"119.904299029599500\",\"point_y\":\"32.366367039986550\"},{\"point_x\":\"121.068949006497500\",\"point_y\":\"31.957479007542150\"},{\"point_x\":\"120.999902002513500\",\"point_y\":\"32.033502001315350\"},{\"point_x\":\"120.700804566000000\",\"point_y\":\"32.114334304500000\"},{\"point_x\":\"120.126363905974500\",\"point_y\":\"32.587577839199550\"},{\"point_x\":\"120.753122005611500\",\"point_y\":\"31.798830002546300\"},{\"point_x\":\"118.924668493985500\",\"point_y\":\"34.865430119227050\"},{\"point_x\":\"119.619737289020500\",\"point_y\":\"32.289095340741900\"},{\"point_x\":\"119.684974007308500\",\"point_y\":\"33.970840007066750\"},{\"point_x\":\"120.582913906274130\",\"point_y\":\"30.986831602436391\"},{\"point_x\":\"120.475370934061500\",\"point_y\":\"31.239636907406500\"},{\"point_x\":\"120.246894005686000\",\"point_y\":\"33.417272005230200\"},{\"point_x\":\"120.278438005596500\",\"point_y\":\"34.031873002648350\"},{\"point_x\":\"120.475290296217500\",\"point_y\":\"31.239601305037800\"},{\"point_x\":\"120.132159002125500\",\"point_y\":\"33.372917000204350\"},{\"point_x\":\"117.978332242063500\",\"point_y\":\"33.752635101126050\"},{\"point_x\":\"120.136144001037000\",\"point_y\":\"33.371339004486800\"},{\"point_x\":\"120.137771002948500\",\"point_y\":\"33.372487004846350\"},{\"point_x\":\"120.137650001794000\",\"point_y\":\"33.373459000140450\"},{\"point_x\":\"120.145881999284000\",\"point_y\":\"33.381818000227250\"},{\"point_x\":\"120.439742006361500\",\"point_y\":\"32.546800002455750\"},{\"point_x\":\"120.141129005700500\",\"point_y\":\"33.396533001214250\"},{\"point_x\":\"119.460514005273500\",\"point_y\":\"31.690108004957450\"},{\"point_x\":\"119.442309007048500\",\"point_y\":\"31.687964007258450\"},{\"point_x\":\"117.990007395977500\",\"point_y\":\"33.881460099021100\"},{\"point_x\":\"121.051577043152000\",\"point_y\":\"31.929531189422650\"},{\"point_x\":\"120.993086744261000\",\"point_y\":\"31.618906273677800\"},{\"point_x\":\"119.044589024129500\",\"point_y\":\"34.961309287584050\"},{\"point_x\":\"119.905732448448040\",\"point_y\":\"31.814241248394952\"},{\"point_x\":\"120.385629333933500\",\"point_y\":\"32.463930699843100\"},{\"point_x\":\"120.111493006348500\",\"point_y\":\"33.381261005997650\"},{\"point_x\":\"120.137299004942000\",\"point_y\":\"33.376917000859950\"},{\"point_x\":\"120.134979002177500\",\"point_y\":\"33.378224007785350\"},{\"point_x\":\"120.137790005654000\",\"point_y\":\"33.377185005694650\"},{\"point_x\":\"118.878980873049685\",\"point_y\":\"31.957064669268532\"},{\"point_x\":\"120.099597007036500\",\"point_y\":\"33.376186005771150\"},{\"point_x\":\"120.273014731667000\",\"point_y\":\"31.611952521052500\"},{\"point_x\":\"120.829403001500000\",\"point_y\":\"32.146308001500000\"},{\"point_x\":\"120.131103005260500\",\"point_y\":\"33.375917002558700\"},{\"point_x\":\"119.187820501507500\",\"point_y\":\"32.479975208456250\"},{\"point_x\":\"120.235918018193000\",\"point_y\":\"32.512550671536000\"},{\"point_x\":\"121.103421002626500\",\"point_y\":\"32.243709001690150\"},{\"point_x\":\"121.098183006048500\",\"point_y\":\"32.237605001777450\"},{\"point_x\":\"120.170711082439400\",\"point_y\":\"31.870289921248582\"},{\"point_x\":\"118.577513005584500\",\"point_y\":\"34.685184005647900\"},{\"point_x\":\"120.184174004942000\",\"point_y\":\"33.335261002182950\"},{\"point_x\":\"119.856091023173000\",\"point_y\":\"31.933267140538700\"},{\"point_x\":\"119.853367696366500\",\"point_y\":\"31.940346479628650\"},{\"point_x\":\"119.671740987278500\",\"point_y\":\"31.428284203317750\"},{\"point_x\":\"119.018882802553500\",\"point_y\":\"33.008989754048650\"},{\"point_x\":\"119.610369000584000\",\"point_y\":\"32.333125002682200\"},{\"point_x\":\"119.611051004380000\",\"point_y\":\"32.271425005048500\"},{\"point_x\":\"120.620037374594723\",\"point_y\":\"31.563570657089541\"},{\"point_x\":\"119.577050007879500\",\"point_y\":\"33.890526000410350\"},{\"point_x\":\"117.463188901000000\",\"point_y\":\"34.406027558000000\"},{\"point_x\":\"120.570135999471000\",\"point_y\":\"32.103475000709250\"},{\"point_x\":\"120.618738839792000\",\"point_y\":\"31.112048900663350\"},{\"point_x\":\"120.618540011609500\",\"point_y\":\"31.112132317084750\"},{\"point_x\":\"120.106077007949500\",\"point_y\":\"33.358454000204850\"},{\"point_x\":\"120.106225006282500\",\"point_y\":\"33.358083002269250\"},{\"point_x\":\"119.570597007870500\",\"point_y\":\"33.528821006417250\"},{\"point_x\":\"119.612680286170500\",\"point_y\":\"33.504461356202750\"},{\"point_x\":\"120.183710005134500\",\"point_y\":\"33.336268007755250\"},{\"point_x\":\"120.184654999524500\",\"point_y\":\"33.334609005600200\"},{\"point_x\":\"121.238131540000000\",\"point_y\":\"31.890194320000000\"},{\"point_x\":\"120.009283006191500\",\"point_y\":\"32.515674002468550\"},{\"point_x\":\"120.882202146372000\",\"point_y\":\"31.462823070354250\"},{\"point_x\":\"120.440151100578500\",\"point_y\":\"32.442384405646850\"},{\"point_x\":\"121.109419255276458\",\"point_y\":\"31.439666569905317\"},{\"point_x\":\"118.955374222285500\",\"point_y\":\"34.860609518375750\"},{\"point_x\":\"121.109211450179365\",\"point_y\":\"31.439664068766914\"},{\"point_x\":\"120.184034001082000\",\"point_y\":\"33.336025007069100\"},{\"point_x\":\"118.806765665327000\",\"point_y\":\"31.878307470238950\"},{\"point_x\":\"121.058119006455000\",\"point_y\":\"32.072709005326050\"},{\"point_x\":\"120.183965001255000\",\"point_y\":\"33.336749002337450\"},{\"point_x\":\"119.110932007432000\",\"point_y\":\"33.934649001806950\"},{\"point_x\":\"120.735140253269500\",\"point_y\":\"32.468524280513800\"},{\"point_x\":\"116.539202999323500\",\"point_y\":\"34.723045006394350\"},{\"point_x\":\"120.184120006859500\",\"point_y\":\"33.336706999689350\"},{\"point_x\":\"120.809814006090500\",\"point_y\":\"31.676327999681250\"},{\"point_x\":\"120.940831080000000\",\"point_y\":\"31.697802955000000\"},{\"point_x\":\"121.179135639581500\",\"point_y\":\"31.547028740446600\"},{\"point_x\":\"120.477453726000000\",\"point_y\":\"31.218620721000000\"},{\"point_x\":\"120.437390861855000\",\"point_y\":\"33.004524710527900\"},{\"point_x\":\"120.441441042042500\",\"point_y\":\"31.957352711153850\"},{\"point_x\":\"120.441850841284000\",\"point_y\":\"31.954372799917500\"},{\"point_x\":\"120.186400000006000\",\"point_y\":\"33.335094999521950\"},{\"point_x\":\"120.186733007431000\",\"point_y\":\"33.336310006678100\"},{\"point_x\":\"120.437058453431298\",\"point_y\":\"31.947719017101564\"},{\"point_x\":\"120.350043006241500\",\"point_y\":\"32.722945000976350\"},{\"point_x\":\"120.320383001119000\",\"point_y\":\"31.813746001571450\"},{\"point_x\":\"119.029033765785500\",\"point_y\":\"31.408051802310650\"},{\"point_x\":\"120.551745001227000\",\"point_y\":\"31.894474007189250\"},{\"point_x\":\"120.176384005695500\",\"point_y\":\"33.481895007193050\"},{\"point_x\":\"120.800778991886500\",\"point_y\":\"31.103829548859800\"},{\"point_x\":\"121.235899081566000\",\"point_y\":\"32.329753615915050\"},{\"point_x\":\"120.528576443292000\",\"point_y\":\"33.142572086460350\"},{\"point_x\":\"121.090237542743000\",\"point_y\":\"32.090786454576400\"},{\"point_x\":\"118.514482215706000\",\"point_y\":\"33.945809201694850\"},{\"point_x\":\"118.548757300972000\",\"point_y\":\"33.923041981266900\"},{\"point_x\":\"119.694728005677500\",\"point_y\":\"32.300644002854850\"},{\"point_x\":\"118.498724006116500\",\"point_y\":\"31.986207004636550\"},{\"point_x\":\"121.201910271122000\",\"point_y\":\"32.135430264997800\"},{\"point_x\":\"121.043373437563000\",\"point_y\":\"31.317422459491400\"},{\"point_x\":\"120.454525866541000\",\"point_y\":\"33.208702095789550\"},{\"point_x\":\"120.462391592459500\",\"point_y\":\"33.214653788302350\"},{\"point_x\":\"120.536812162211660\",\"point_y\":\"31.948778339113147\"},{\"point_x\":\"120.941914297060500\",\"point_y\":\"31.928542537503600\"},{\"point_x\":\"120.861802241803500\",\"point_y\":\"31.990059991646450\"},{\"point_x\":\"120.861093862717500\",\"point_y\":\"31.990003597245700\"},{\"point_x\":\"119.821273146635500\",\"point_y\":\"32.709727867173150\"},{\"point_x\":\"118.860029676006510\",\"point_y\":\"32.004457328275361\"},{\"point_x\":\"118.988718425824963\",\"point_y\":\"32.143161999745134\"},{\"point_x\":\"118.846858039998375\",\"point_y\":\"32.007202735387359\"},{\"point_x\":\"119.552036885027500\",\"point_y\":\"32.008829889136650\"},{\"point_x\":\"119.689577395000000\",\"point_y\":\"31.835842823000000\"},{\"point_x\":\"119.729937921000000\",\"point_y\":\"33.189978380000000\"},{\"point_x\":\"121.072102241734343\",\"point_y\":\"31.608024017597810\"},{\"point_x\":\"118.232711027218500\",\"point_y\":\"33.950602007140700\"},{\"point_x\":\"117.191196886397000\",\"point_y\":\"34.272053069868100\"},{\"point_x\":\"119.180768015663500\",\"point_y\":\"34.591639442624050\"},{\"point_x\":\"117.176193925546500\",\"point_y\":\"34.275135851651000\"},{\"point_x\":\"120.367120129687000\",\"point_y\":\"31.486422734493450\"},{\"point_x\":\"119.189386059556000\",\"point_y\":\"34.587624354051400\"},{\"point_x\":\"119.417989150000000\",\"point_y\":\"32.225757520000000\"},{\"point_x\":\"120.301905183717000\",\"point_y\":\"31.676648829979750\"},{\"point_x\":\"116.599421399413500\",\"point_y\":\"34.687914088378700\"},{\"point_x\":\"117.009838845054500\",\"point_y\":\"34.513156979291900\"},{\"point_x\":\"117.009992670878500\",\"point_y\":\"34.513031005385900\"},{\"point_x\":\"117.151314558828500\",\"point_y\":\"34.310368906652600\"},{\"point_x\":\"117.151211077819000\",\"point_y\":\"34.310303641897150\"},{\"point_x\":\"117.271786729586530\",\"point_y\":\"34.277793356107491\"},{\"point_x\":\"118.734778668500000\",\"point_y\":\"32.054487758500000\"},{\"point_x\":\"119.402563000000000\",\"point_y\":\"32.335700000000050\"},{\"point_x\":\"117.144997773656000\",\"point_y\":\"34.271180498081700\"},{\"point_x\":\"119.964049700000000\",\"point_y\":\"32.530792610000000\"},{\"point_x\":\"119.611888266500000\",\"point_y\":\"34.159626757000000\"},{\"point_x\":\"120.276989485027500\",\"point_y\":\"31.991399815654750\"},{\"point_x\":\"120.131722274798500\",\"point_y\":\"32.492485204668750\"},{\"point_x\":\"117.334586797992940\",\"point_y\":\"34.249499900322690\"},{\"point_x\":\"117.361838509247000\",\"point_y\":\"34.257323018125300\"},{\"point_x\":\"119.895375576701000\",\"point_y\":\"32.457709482738600\"},{\"point_x\":\"120.195918581859603\",\"point_y\":\"31.625230461661088\"},{\"point_x\":\"120.286655680842500\",\"point_y\":\"32.016742697871850\"},{\"point_x\":\"120.638373006548500\",\"point_y\":\"32.224889551414450\"},{\"point_x\":\"121.668586996020000\",\"point_y\":\"31.790112642121100\"},{\"point_x\":\"120.403944991188000\",\"point_y\":\"31.769976487526700\"},{\"point_x\":\"119.888167943559000\",\"point_y\":\"31.473484820000000\"},{\"point_x\":\"120.586504553300000\",\"point_y\":\"32.369766166700050\"},{\"point_x\":\"119.818382925091000\",\"point_y\":\"31.362484462035150\"},{\"point_x\":\"120.285016290489500\",\"point_y\":\"32.019717956031150\"},{\"point_x\":\"119.675663400000000\",\"point_y\":\"31.347125950000000\"},{\"point_x\":\"119.160151100000000\",\"point_y\":\"31.338990989999950\"},{\"point_x\":\"119.834358937317000\",\"point_y\":\"31.338580935363900\"},{\"point_x\":\"120.318856599931423\",\"point_y\":\"31.523597596099749\"},{\"point_x\":\"120.471484015545000\",\"point_y\":\"32.519251591106700\"},{\"point_x\":\"119.651286700000000\",\"point_y\":\"31.392349790000000\"},{\"point_x\":\"120.278857487275000\",\"point_y\":\"31.995056749985200\"},{\"point_x\":\"119.634674514275000\",\"point_y\":\"31.280198356590900\"},{\"point_x\":\"119.917599029741500\",\"point_y\":\"31.354651834838100\"},{\"point_x\":\"120.277683200895000\",\"point_y\":\"31.992722814975600\"},{\"point_x\":\"121.194950734104000\",\"point_y\":\"31.894826985536700\"},{\"point_x\":\"120.975085146517500\",\"point_y\":\"31.929951051446300\"},{\"point_x\":\"119.911310292914000\",\"point_y\":\"32.499173554332400\"},{\"point_x\":\"120.902631839678000\",\"point_y\":\"32.038575324548600\"},{\"point_x\":\"120.514018258312000\",\"point_y\":\"32.613124858601100\"},{\"point_x\":\"119.833031500000000\",\"point_y\":\"31.349823680000000\"},{\"point_x\":\"120.975440885414000\",\"point_y\":\"32.010988269488500\"},{\"point_x\":\"120.463061199722000\",\"point_y\":\"31.749345574507600\"},{\"point_x\":\"119.828771200000000\",\"point_y\":\"31.349963729999950\"},{\"point_x\":\"121.789137678186000\",\"point_y\":\"31.721819351615400\"},{\"point_x\":\"120.462308224288000\",\"point_y\":\"32.552213197815450\"},{\"point_x\":\"120.118235403612500\",\"point_y\":\"31.976543728552200\"},{\"point_x\":\"121.605161419628000\",\"point_y\":\"32.064177581199900\"},{\"point_x\":\"121.199351904358000\",\"point_y\":\"31.879049662132400\"},{\"point_x\":\"121.199731113629000\",\"point_y\":\"31.878819461112550\"},{\"point_x\":\"121.910169654626000\",\"point_y\":\"31.723940268625850\"},{\"point_x\":\"120.304890600000000\",\"point_y\":\"31.669171620000000\"},{\"point_x\":\"120.195389641313000\",\"point_y\":\"31.625302928498850\"},{\"point_x\":\"120.305521730387000\",\"point_y\":\"31.649546551402450\"},{\"point_x\":\"120.311853010000000\",\"point_y\":\"31.592145470000000\"},{\"point_x\":\"121.221534811075000\",\"point_y\":\"31.895430784443600\"},{\"point_x\":\"121.218663677249000\",\"point_y\":\"31.891240077186550\"},{\"point_x\":\"119.042802743673500\",\"point_y\":\"32.119894437641950\"},{\"point_x\":\"121.178456173665700\",\"point_y\":\"32.336353284850143\"},{\"point_x\":\"121.178458716790190\",\"point_y\":\"32.336290714216333\"},{\"point_x\":\"119.932690255960000\",\"point_y\":\"31.469678014681200\"},{\"point_x\":\"121.035403417500000\",\"point_y\":\"32.072990816000000\"},{\"point_x\":\"120.928275523039500\",\"point_y\":\"32.060860512460900\"},{\"point_x\":\"119.932097194971500\",\"point_y\":\"31.467822843438600\"},{\"point_x\":\"119.861835300000000\",\"point_y\":\"31.410419780000050\"},{\"point_x\":\"119.984403678721000\",\"point_y\":\"32.417274860541200\"},{\"point_x\":\"119.902192000000000\",\"point_y\":\"32.263287000000000\"},{\"point_x\":\"119.970620021500000\",\"point_y\":\"32.415198601500000\"},{\"point_x\":\"119.932864149518050\",\"point_y\":\"32.495531314731464\"},{\"point_x\":\"119.962937376241000\",\"point_y\":\"32.528656657502800\"},{\"point_x\":\"120.927162686647500\",\"point_y\":\"32.060565627256200\"},{\"point_x\":\"120.431940498606000\",\"point_y\":\"32.543021157716100\"},{\"point_x\":\"119.891056000000000\",\"point_y\":\"32.473343000000000\"},{\"point_x\":\"119.908942561732000\",\"point_y\":\"32.492136226554800\"},{\"point_x\":\"119.125625044393500\",\"point_y\":\"31.954445374599750\"},{\"point_x\":\"119.967554188595000\",\"point_y\":\"32.530867801133350\"},{\"point_x\":\"121.198726964555000\",\"point_y\":\"31.870277767924050\"},{\"point_x\":\"121.198784887098500\",\"point_y\":\"31.870505001086300\"},{\"point_x\":\"120.507385000000000\",\"point_y\":\"31.803136000000000\"},{\"point_x\":\"120.911039513557000\",\"point_y\":\"32.044954872747000\"}]";
        JSONArray json=JSONArray.parseArray(arrMap);
        List<Map> mapList=new ArrayList<>();
        for(int i=0;i<json.size();i++) {
            JSONObject jsonstr=JSONObject.parseObject(json.getString(i));
            Map map=new HashMap();
            map.put("point_x",jsonstr.get("point_x"));
            map.put("point_y",jsonstr.get("point_y"));
            mapList.add(map);
        }
        //List<Map> mapList =null;
        System.out.println("******************************打点数为："+mapList.size());
        for(Map map:mapList){
            LatLng targetPos1 = new LatLng(Double.parseDouble(map.get("point_y").toString()),Double.parseDouble(map.get("point_x").toString()));
            MarkerOptions markerOptions = new MarkerOptions().position(targetPos1);
           /* markerOptions.getMarker().setIcon(IconFactory.getInstance(InitManageActivity.this).fromResource(R.drawable.blue_circle));
            markerOptions.getMarker().setTitle("测试点X");
            markerOptions.getMarker().setDraggable(true);*/
            markerOptions.setIcon(IconFactory.getInstance(InitMarkerActivity.this).fromResource(R.drawable.blue_circle));
            markerOptions.setTitle("title");
            markerOptions.setSnippet("snippet");
            markerOptions.getMarker().setTitle("123");
            markerOptionsList.add(markerOptions);
        }

        List<Marker> markers = aegisMap.addMarkers(markerOptionsList);
        /*for (Marker m:markers){
            m.setIcon(IconFactory.getInstance(InitManageActivity.this).fromResource(R.drawable.blue_circle));
            m.setTitle("测试点X");
            m.setDraggable(true);
        }*/
    }


    @Override
    public boolean onMapClick(@NonNull LatLng point) {
        if (mapView != null) {
            mapView.getMapAsync(aegisMap -> {
                if (!isAdded) {
                    LatLng latLng =point;
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.icon(IconFactory.getInstance(InitMarkerActivity.this).fromResource(R.drawable.select_point));
                    // 默认传入坐标为思极坐标
                    markerOptions.position(latLng);
                    markerOptions.snippet("我被点击了");
                    marker = aegisMap.addMarker(markerOptions);
                    marker.setDraggable(true);
                    marker.setIcon(IconFactory.getInstance(InitMarkerActivity.this).fromResource(R.drawable.miami_beach));
                    aegisMap.updateMarker(marker);
                    marker.setIcon(IconFactory.getInstance(InitMarkerActivity.this).fromResource(R.drawable.blue_circle));
                    aegisMap.updateMarker(marker);
                    // 移动到当前点位置
                    aegisMap.setCameraPosition(new CameraPosition.Builder().target(latLng).build());
                    aegisMap.selectMarker(marker);
                    isAdded = false;
                }
            });
        }
        return true;
    }
    /**
     * 点击按钮添加图标回调
     *
     * @param view
     */
    public void onAddMarker(View view) {
        if (mapView != null) {
            mapView.getMapAsync(aegisMap -> {
                if (!isAdded) {
                    LatLng latLng = new LatLng(34.559173002839050, 116.561986006796500);
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.icon(IconFactory.getInstance(InitMarkerActivity.this).fromResource(R.drawable.select_point));
                    // 默认传入坐标为思极坐标
                    markerOptions.position(latLng);
                    markerOptions.title("思极地图");
                    markerOptions.snippet("我被点击了");
                    marker = aegisMap.addMarker(markerOptions);
                    marker.setDraggable(true);
                    marker.setIcon(IconFactory.getInstance(InitMarkerActivity.this).fromResource(R.drawable.miami_beach));
                    aegisMap.updateMarker(marker);
                    marker.setIcon(IconFactory.getInstance(InitMarkerActivity.this).fromResource(R.drawable.blue_circle));
                    aegisMap.updateMarker(marker);
                    // 移动到当前点位置
                    aegisMap.setCameraPosition(new CameraPosition.Builder().target(latLng).build());
                    isAdded = true;
                }
            });
        }
    }

    /**
     * 点击按钮移除图标回调
     *
     * @param view
     */
    public void onRemoveMarker(View view) {
        if (mapView != null) {
            mapView.getMapAsync(aegisMap -> {
                if (marker != null && isAdded) {
                    aegisMap.removeMarker(marker);
                    isAdded = false;
                }
            });
        }
    }

    /**
     * 点击按钮移除所有标注回调
     *
     * @param view
     */
    public void onRemoveMarkers(View view) {
        if (mapView != null) {
            mapView.getMapAsync(aegisMap -> {
                aegisMap.removeAnnotations();
                isAdded = false;
            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_goback:
                finish();
                break;
            default:
                break;
        }
    }

    private final ValueAnimator.AnimatorUpdateListener animatorUpdateListener =
            new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    LatLng animatedPosition = (LatLng) valueAnimator.getAnimatedValue();
                    geoJsonSource.setGeoJson(Point.fromLngLat(animatedPosition.getLongitude(), animatedPosition.getLatitude()));
                }
            };

    private static final TypeEvaluator<LatLng> latLngEvaluator = new TypeEvaluator<LatLng>() {

        private final LatLng latLng = new LatLng();

        @Override
        public LatLng evaluate(float fraction, LatLng startValue, LatLng endValue) {
            latLng.setLatitude(startValue.getLatitude()
                    + ((endValue.getLatitude() - startValue.getLatitude()) * fraction));
            latLng.setLongitude(startValue.getLongitude()
                    + ((endValue.getLongitude() - startValue.getLongitude()) * fraction));
            return latLng;
        }
    };
}