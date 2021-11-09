package com.zaaach.linematchingview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * @Author: Zaaach
 * @Date: 2021/11/4
 * @Email: zaaach@aliyun.com
 * @Description: 连线题，对左右两列进行连线
 */
public class LineMatchingView<T> extends ViewGroup {
    private static final int DEFAULT_HORIZONTAL_PADDING = 180;
    private static final int DEFAULT_VERTICAL_PADDING = 48;
    private static final int DEFAULT_ITEM_WIDTH = 300;
    private static final int DEFAULT_ITEM_HEIGHT = 120;
    private static final int DEFAULT_LINE_WIDTH = 6;

    //item state
    public static final int NORMAL  = 100;
    public static final int CHECKED = 101;
    public static final int LINED   = 102;
    public static final int CORRECT = 103;
    public static final int ERROR   = 104;

    private int horizontalPadding;
    private int verticalPadding;
    private int itemWidth;
    private int itemHeight;
    private float lineWidth;
    private int lineNormalColor;
    private int lineCorrectColor;
    private int lineErrorColor;
    private Paint linePaint;
    private int leftMaxWidth;

    private List<LinkableWrapper> leftItems;
    private List<LinkableWrapper> rightItems;
    private final List<Line> oldLines = new ArrayList<>();
    private final List<Line> newLines = new ArrayList<>();
    private LinkableAdapter<T> linkableAdapter;
    private int currLeftChecked = -1;
    private int currRightChecked = -1;
    private boolean finished;
    private int resultSize;

    public LineMatchingView(Context context) {
        this(context, null);
    }

    public LineMatchingView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LineMatchingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public LineMatchingView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    public LineMatchingView<T> init(@NonNull LinkableAdapter<T> adapter){
        this.linkableAdapter = adapter;
        return this;
    }

    public void restore(){
        finished = false;
        oldLines.addAll(newLines);
        newLines.clear();
        invalidate();
        if (leftItems != null){
            for (int i = 0; i < leftItems.size(); i++) {
                leftItems.get(i).lined = false;
                leftItems.get(i).line = null;
                notifyItemStateChanged(i, NORMAL, true);
            }
        }
        if (rightItems != null){
            for (int i = 0; i < rightItems.size(); i++) {
                rightItems.get(i).lined = false;
                rightItems.get(i).line = null;
                notifyItemStateChanged(i, NORMAL, false);
            }
        }
    }

    public boolean isFinished(){
        return finished;
    }

    public void setItems(@NonNull List<T> left, @NonNull List<T> right){
        if (linkableAdapter == null) {
            throw new IllegalStateException("LinkableAdapter must not be null, please see method init()");
        }
        leftItems = new ArrayList<>();
        rightItems = new ArrayList<>();
        addItems(left, true);
        addItems(right, false);
        resultSize = Math.min(leftItems.size(), rightItems.size());
    }

    private void addItems(List<T> list, boolean isLeft){
        for (int i = 0; i < list.size(); i++) {
            T item = list.get(i);
            int type = linkableAdapter.getItemType(item, i);
            View view = linkableAdapter.getView(item, this, type, i);
            addView(view);
            int index = i;
            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (finished) return;
                    if (isLeft) {
                        //先恢复上个点击的item状态
                        if (currLeftChecked >= 0) {
                            notifyItemStateChanged(currLeftChecked, leftItems.get(currLeftChecked).lined ? LINED : NORMAL, true);
                        }
                        if (currLeftChecked == index) {
                            currLeftChecked = -1;
                        } else {
                            currLeftChecked = index;
                            notifyItemStateChanged(index, CHECKED, true);
                            drawLineBetween(currLeftChecked, currRightChecked);
                        }
                    }else {
                        if (currRightChecked >= 0) {
                            notifyItemStateChanged(currRightChecked, rightItems.get(currRightChecked).lined ? LINED : NORMAL, false);
                        }
                        if (currRightChecked == index){
                            currRightChecked = -1;
                        }else {
                            currRightChecked = index;
                            notifyItemStateChanged(index, CHECKED, false);
                            drawLineBetween(currLeftChecked, currRightChecked);
                        }
                    }
                }
            });
            LinkableWrapper wrapper = new LinkableWrapper();
            wrapper.item = item;
            wrapper.view = view;
            if (isLeft){
                leftItems.add(wrapper);
            }else {
                rightItems.add(wrapper);
            }
        }
    }

    private void drawLineBetween(int leftIndex, int rightIndex){
        if (leftIndex < 0 || rightIndex < 0) return;
        //移除旧的连线
        LinkableWrapper leftItem = leftItems.get(leftIndex);
        if (leftItem.lined){
            Line oldLine = leftItem.line;
            if (oldLine != null){
                oldLines.add(oldLine);
                setLined(oldLine.end, false, false);
                notifyItemStateChanged(oldLine.end, NORMAL, false);
            }
        }
        LinkableWrapper rightItem = rightItems.get(rightIndex);
        if (rightItem.lined){
            Line oldLine = rightItem.line;
            if (oldLine != null){
                oldLines.add(oldLine);
                setLined(oldLine.start, false, true);
                notifyItemStateChanged(oldLine.start, NORMAL, true);
            }
        }
        if (leftItem.lined || rightItem.lined) {
            for (Iterator<Line> iterator = newLines.iterator(); iterator.hasNext(); ) {
                Line line = iterator.next();
                if (line.equals(leftItem.line) || line.equals(rightItem.line)) {
                    iterator.remove();
                }
            }
        }
        //生成新的连线
        Line newLine = new Line(leftItem.pointX, leftItem.pointY, rightItem.pointX, rightItem.pointY);
        newLine.start = leftIndex;
        newLine.end = rightIndex;
        newLine.color = lineNormalColor;
        newLines.add(newLine);
        leftItem.lined = true;
        rightItem.lined = true;
        notifyItemStateChanged(leftIndex, LINED, true);
        notifyItemStateChanged(rightIndex, LINED, false);
        //重置
        currLeftChecked = -1;
        currRightChecked = -1;
        if (resultSize == newLines.size()){
            finished = true;
            checkResult();
        }
        invalidate();
        leftItem.line = newLine;
        rightItem.line = newLine;
    }

    private void checkResult() {
        for (Line line : newLines) {
            int l = line.start;
            int r = line.end;
            if (linkableAdapter != null){
                if (linkableAdapter.isCorrect(leftItems.get(l).item, rightItems.get(r).item, l, r)){
                    line.color = lineCorrectColor;
                    notifyItemStateChanged(l, CORRECT, true);
                    notifyItemStateChanged(r, CORRECT, false);
                }else {
                    line.color = lineErrorColor;
                    notifyItemStateChanged(l, ERROR, true);
                    notifyItemStateChanged(r, ERROR, false);
                }
            }
        }
    }

    private void setLined(int position, boolean lined, boolean isLeft){
        if (position < 0) return;
        if (isLeft){
            leftItems.get(position).lined = lined;
        }else {
            rightItems.get(position).lined = lined;
        }
    }

    private void notifyItemStateChanged(int position, int state, boolean isLeft){
        if (linkableAdapter != null && position >= 0){
            LinkableWrapper wrapper = isLeft ? leftItems.get(position) : rightItems.get(position);
            linkableAdapter.onItemStateChanged(wrapper.item, wrapper.view, state, position);
        }
    }

    private void init(Context context, AttributeSet attrs){
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.LineMatchingView);
        horizontalPadding = array.getDimensionPixelSize(R.styleable.LineMatchingView_lmv_horizontal_padding, DEFAULT_HORIZONTAL_PADDING);
        verticalPadding = array.getDimensionPixelSize(R.styleable.LineMatchingView_lmv_vertical_padding, DEFAULT_VERTICAL_PADDING);
        itemWidth = array.getDimensionPixelSize(R.styleable.LineMatchingView_lmv_item_width, DEFAULT_ITEM_WIDTH);
        itemHeight = array.getDimensionPixelSize(R.styleable.LineMatchingView_lmv_item_height, DEFAULT_ITEM_HEIGHT);
        lineWidth = array.getDimensionPixelSize(R.styleable.LineMatchingView_lmv_line_width, DEFAULT_LINE_WIDTH);
        lineNormalColor = array.getColor(R.styleable.LineMatchingView_lmv_line_color_normal, Color.GRAY);
        lineCorrectColor = array.getColor(R.styleable.LineMatchingView_lmv_line_color_correct, Color.GREEN);
        lineErrorColor = array.getColor(R.styleable.LineMatchingView_lmv_line_color_error, Color.RED);
        array.recycle();

        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setStyle(Paint.Style.FILL);
        linePaint.setStrokeWidth(lineWidth);
        linePaint.setColor(lineNormalColor);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        int[] measuredLeftSize = measureColumn(leftItems, widthMeasureSpec, heightMeasureSpec);
        int measuredLeftWidth = measuredLeftSize[0];
        int measuredLeftHeight = measuredLeftSize[1];
        leftMaxWidth = measuredLeftSize[0];

        int[] measuredRightSize = measureColumn(rightItems, widthMeasureSpec, heightMeasureSpec);
        int measuredRightWidth = measuredRightSize[0];
        int measuredRightHeight = measuredRightSize[1];

        int wMode = MeasureSpec.getMode(widthMeasureSpec);
        int hMode = MeasureSpec.getMode(heightMeasureSpec);
        setMeasuredDimension(
                wMode == MeasureSpec.EXACTLY ? width : measuredLeftWidth + measuredRightWidth + getPaddingLeft() + getPaddingRight() + horizontalPadding,
                hMode == MeasureSpec.EXACTLY ? height : Math.max(measuredLeftHeight, measuredRightHeight) + getPaddingTop() + getPaddingBottom());
    }

    private int[] measureColumn(List<LinkableWrapper> list, int widthMeasureSpec, int heightMeasureSpec){
        int measuredWidth = 0;
        int measuredHeight = 0;
        for (int i = 0; i < list.size(); i++) {
            LinkableWrapper wrapper = list.get(i);
            View child = wrapper.view;
            LayoutParams lp = child.getLayoutParams();
            if (lp != null){
                if (itemWidth > 0){
                    lp.width = itemWidth;
                }
                if (itemHeight > 0){
                    lp.height = itemHeight;
                }
            }
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
            measuredWidth = Math.max(measuredWidth, child.getMeasuredWidth());
            measuredHeight += child.getMeasuredHeight() + (i > 0 ? verticalPadding : 0);
        }
        return new int[]{measuredWidth, measuredHeight};
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        doLayout(leftItems, getPaddingLeft(), getPaddingTop(), true);
        doLayout(rightItems, getPaddingLeft() + leftMaxWidth + horizontalPadding, getPaddingTop(), false);
    }

    private void doLayout(List<LinkableWrapper> list, int left, int top, boolean isLeft){
        if (list == null) return;
        for (int i = 0; i < list.size(); i++) {
            LinkableWrapper wrapper = list.get(i);
            View view = wrapper.view;
            int w = view.getMeasuredWidth();
            int h = view.getMeasuredHeight();
            view.layout(left, top, left + w, top + h);
            if (linkableAdapter != null){
                linkableAdapter.onBindView(wrapper.item, view, i);
            }
            wrapper.pointX = isLeft ? left + w : left;
            wrapper.pointY = top + h / 2f;
            top += h + verticalPadding;
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        linePaint.setColor(Color.TRANSPARENT);
        for (Line line : oldLines) {
            canvas.drawLine(line.startX, line.startY, line.endX, line.endY, linePaint);
        }
        oldLines.clear();
        for (Line line : newLines) {
            linePaint.setColor(line.color);
            canvas.drawLine(line.startX, line.startY, line.endX, line.endY, linePaint);
        }
    }

    private static class Line {
        public float startX;
        public float startY;
        public float endX;
        public float endY;
        public int color;
        public int start;
        public int end;

        public Line(float startX, float startY, float endX, float endY) {
            this.startX = startX;
            this.startY = startY;
            this.endX = endX;
            this.endY = endY;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Line line = (Line) o;
            return Float.compare(line.startX, startX) == 0
                    && Float.compare(line.startY, startY) == 0
                    && Float.compare(line.endX, endX) == 0
                    && Float.compare(line.endY, endY) == 0;
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(new float[]{startX, startY, endX, endY});
        }
    }

    private class LinkableWrapper {
        public Line line;
        public float pointX;
        public float pointY;
        public boolean lined;
        public View view;
        public T item;
    }

    public interface LinkableAdapter<T> {
        View getView(T item, ViewGroup parent, int itemType, int position);
        int getItemType(T item, int position);
        void onBindView(T item, View view, int position);
        void onItemStateChanged(T item, View view, int state, int position);
        boolean isCorrect(T left, T right, int l, int r);
    }
}