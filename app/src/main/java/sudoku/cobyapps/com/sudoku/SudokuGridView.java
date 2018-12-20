package sudoku.cobyapps.com.sudoku;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;

public class SudokuGridView extends View {
    private Paint paintLines;
    private Paint paintSelections;
    private float width;
    private float height;
    private Rect selectionRectangle;
    private SudokuDataHolder dataHolder;
    private SelectionCoordinates selectionCoordinates;
    public SudokuGridView(Context context, SudokuDataHolder dataHolder) {
        super(context);
        paintLines = new Paint();
        paintSelections = new Paint();
        selectionRectangle = new Rect();
        setFocusable(true);
        setFocusableInTouchMode(true);
        this.dataHolder = dataHolder;
        selectionCoordinates = new SelectionCoordinates();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for(int i = 0; i < 9; i++){
            if (i % 3 == 0) {
                paintLines.setStrokeWidth(3f);
            }
            canvas.drawLine(0,i*width, getWidth(),i*width, paintLines);
            canvas.drawLine(i*width,0, i*width,height*9, paintLines);
            paintLines.setStrokeWidth(0f);
        }
            paintLines.setStrokeWidth(3f);
            canvas.drawLine(0,9*width, getWidth(),9*width, paintLines);
            paintLines.setStrokeWidth(0f);
            paintSelections.setColor(Color.parseColor("#ffc0c0"));
            canvas.drawRect(selectionRectangle, paintSelections);

            Paint paint5 = new Paint();
            //paint5.setColor(Color.BLACK);
            //paint5.setStyle(Paint.Style.FILL);
            paint5.setTextSize(height * 0.75f);
            paint5.setTextScaleX(width / height);
            paint5.setTextAlign(Paint.Align.CENTER);
            Paint.FontMetrics fm = paint5.getFontMetrics();
            float x = width / 2;
            float y = height / 2 - (fm.ascent + fm.descent) / 2;
            //paint5.setTextSize(height * 0.75f);
            for(int i = 0; i < 9; i++){
                for(int j = 0; j < 9; j++){
                    if(dataHolder.getGrid()[i][j].getNumber()!=SudokuDataHolder.NUMBER_EMPTY){
                        if(dataHolder.getGrid()[i][j].getIsGiven()){
                            paint5.setColor(Color.parseColor("#1300e1"));
                        }
                        float a = i * width + y;
                        float b = j * height + x;
                        canvas.drawText(dataHolder.getGrid()[i][j].getNumber()+"", b, a, paint5);
                        paint5.setColor(Color.BLACK);
                    }else if(!dataHolder.getGrid()[i][j].getNotes().equals("")){
                        String [] notes = dataHolder.getGrid()[i][j].getNotes().split(",");
                        for(int k = 0 ; k < notes.length ; k++){
                            paint5.setTextSize(height * 0.2f);
                            float offsetX = width / 4;
                            float offsetY = ( height / 7 - (fm.ascent + fm.descent) / 7) + 5;
                            int val = Integer.parseInt(notes[k]);
                            if(val==1) {
                                offsetY = offsetY + 2;
                            }
                            if(val==2) {
                                offsetY = offsetY + 2;
                                offsetX = offsetX + offsetX;
                            }
                            if(val==3) {
                                offsetY = offsetY + 2;
                                offsetX = width - offsetX;
                            }

                            if(val==4) {
                                offsetY = offsetY + offsetY + 4;
                            }
                            if(val==5) {
                                offsetX = offsetX + offsetX;
                                offsetY = offsetY + offsetY + 4;
                            }
                            if(val==6) {
                                offsetY = offsetY + offsetY + 4;
                                offsetX = width - offsetX;
                            }
                            if(val==7) {
                                offsetY = height - offsetY + 10;
                            }
                            if(val==8) {
                                offsetX = offsetX + offsetX;
                                offsetY = height - offsetY + 10;
                            }
                            if(val==9) {
                                offsetX = width - offsetX ;
                                offsetY = height - offsetY +10;
                            }
                            float a = i * width + offsetX;
                            float b = j * height + offsetY;
                            canvas.drawText(String.valueOf(val), b, a, paint5);
                            paint5.setTextSize(height * 0.75f);
                        }
                    }
                }
            }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int i = (int) (event.getX() / width);
        int j = (int) (event.getY() / width);
        if(((i < 9) && (j < 9)) && !((i==selectionCoordinates.getX())&&(j==selectionCoordinates.getY()))){
            invalidate(selectionRectangle);
            selectionRectangle.set((int) (i * width + 1), (int) (j * height +1), (int) (i
                    * width + width), (int) (j * height + height));
            selectionCoordinates.setSelectionCoordinates(j,i);
        }else{
            invalidate(selectionRectangle);
            selectionCoordinates.setSelectionCoordinates(SelectionCoordinates.NOT_SELECTED,SelectionCoordinates.NOT_SELECTED);
            selectionRectangle.set(0,0,0,0);
        }
        invalidate(selectionRectangle);
        return super.onTouchEvent(event);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        width = w / 9f;
        height = w / 9f;
        super.onSizeChanged(w, h, oldw, oldh);
    }

    public SelectionCoordinates getSelectionCoordinates() {
        return selectionCoordinates;
    }

    public SudokuDataHolder getDataHolder() {
        return dataHolder;
    }

    public void setDataHolder(SudokuDataHolder dataHolder) {
        this.dataHolder = dataHolder;
    }
}
