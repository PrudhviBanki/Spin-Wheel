package com.prudhvi.spinwheel;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.prudhvi.spinwheel.databinding.FragmentFirstBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import nl.dionsegijn.konfetti.models.Shape;
import nl.dionsegijn.konfetti.models.Size;


public class FirstFragment extends Fragment {
    List<LuckyItem> data = new ArrayList<>();
    private FragmentFirstBinding binding;

    int minDigits = 0;
    int maxDigits = 6;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentFirstBinding.inflate(inflater, container, false);

        binding.buttonFirst.setEnabled(false);
        LuckyItem luckyItem1 = new LuckyItem();
        luckyItem1.t = "0";
        luckyItem1.c = Color.parseColor("#DB4D43");
        data.add(luckyItem1);

        LuckyItem luckyItem2 = new LuckyItem();
        luckyItem2.t = "500";
        luckyItem2.c = Color.parseColor("#FBCE2A");
        luckyItem2.path = "https://bellard.org/bpg/2.png";
        data.add(luckyItem2);

        LuckyItem luckyItem3 = new LuckyItem();
        luckyItem3.st = "Joker";//"1000";
        luckyItem3.path = "https://www.freepnglogos.com/uploads/clown/clown-face-emoji-pictures-free-download-4.png";
        luckyItem3.c = Color.parseColor("#AB61D2");
        data.add(luckyItem3);

        LuckyItem luckyItem4 = new LuckyItem();
        luckyItem4.t = "1M";
        luckyItem4.c = Color.parseColor("#DA7D6E");
        data.add(luckyItem4);

        LuckyItem luckyItem5 = new LuckyItem();
        luckyItem5.st = "iPhone";//500
        luckyItem5.path = "https://pngimg.com/uploads/apple_logo/apple_logo_PNG19694.png";
        luckyItem5.c = Color.parseColor("#F251A7");
        data.add(luckyItem5);

        LuckyItem luckyItem6 = new LuckyItem();
        luckyItem6.t = "10";
        luckyItem6.c = Color.parseColor("#8CD278");
        data.add(luckyItem6);

        LuckyItem luckyItem7 = new LuckyItem();
        //luckyItem7.t = "10k";
        luckyItem7.st = "Doll";
        luckyItem7.path = "https://bellard.org/bpg/2.png";
        luckyItem7.c = Color.parseColor("#43B4F0");
        data.add(luckyItem7);

        LuckyItem luckyItem8 = new LuckyItem();
        luckyItem8.t = "1";
        luckyItem8.path = "https://bellard.org/bpg/5.png";
        luckyItem8.st = "scratch";
        luckyItem8.c = Color.parseColor("#86B4F0");
        data.add(luckyItem8);
        new Handler().postDelayed(() -> {
            binding.luckyWheel.setData(data);
            binding.buttonFirst.setEnabled(true);
        }, 0);
        return binding.getRoot();

    }

    void playAnimationConfetti() {
        binding.confetti.build()
                .addColors(Color.RED, Color.GREEN)
                .setDirection(0.0, 359.0)
                .setSpeed(1f, 10f)
                .setFadeOutEnabled(true)
                .setTimeToLive(120L)
                .addShapes(Shape.Square.INSTANCE, Shape.Circle.INSTANCE)
                .addSizes(new Size(10, 5f))
                .setPosition(-50f, binding.confetti.getWidth() + 50f, -50f, -50f)
                .streamFor(500, 700L);
    }

    private void sc_dialog() {
        Dialog dialog = new BottomSheetDialog(requireActivity());
        dialog.setContentView(R.layout.scratchdialog);
        ((ScratchView) dialog.findViewById(R.id.sct)).setRevealListener(new ScratchView.IRevealListener() {
            @Override
            public void onRevealed(ScratchView scratchView) {
                playAnimationConfetti();
                dialog.dismiss();
            }

            @Override
            public void onRevealPercentChangedListener(ScratchView scratchView, float percent) {
                if (scratchView.isRevealed()) {
                    ((ScratchView) dialog.findViewById(R.id.sct)).clear();
                }

            }
        });
        dialog.show();

    }


    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.luckyWheel.setLuckyWheelBackgrouldColor(Color.parseColor("#60000000"));
        binding.luckyWheel.setBorderWidth(5);
        binding.luckyWheel.setLuckyRoundItemSelectedListener(index -> {
            Toast.makeText(getActivity(), "Target Reached", Toast.LENGTH_SHORT).show();
            playAnimationConfetti();
        });
        binding.gallery.setOnClickListener(v -> sc_dialog());
        binding.buttonFirst.setOnClickListener(view1 -> binding.luckyWheel.startLuckyWheelWithTargetIndex(randomNumber()));
        binding.luckyWheel.setItemSelectedListener(index -> {
            if (index.t != null) {
                Toast.makeText(getActivity(), index.t, Toast.LENGTH_SHORT).show();
            } else {
                Uri imageUri = Uri.parse(index.path);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(imageUri, "image/*");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                if (intent.resolveActivity(requireActivity().getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });
        binding.luckyWheel.invalidate();

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public int randomNumber() {
        Random random = new Random();
        // Generate a random number of digits
        int numDigits = random.nextInt(maxDigits - minDigits + 1) + minDigits;
        return random.nextInt((int) Math.pow(10, numDigits));

    }

    @Override
    public void onResume() {
        super.onResume();
    }

}