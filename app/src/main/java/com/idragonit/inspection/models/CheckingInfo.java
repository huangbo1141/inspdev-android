package com.idragonit.inspection.models;

import com.idragonit.inspection.AppData;
import com.idragonit.inspection.Constants;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by CJH on 2016.01.24.
 */
public class CheckingInfo implements Serializable{

    public String location;
    public boolean isOmit;
    public boolean isFront;
    public boolean isComment;

    public ArrayList<CheckingItemInfo> checking_list;


    public CheckingInfo() {
        location = "";
        isOmit = false;
        isFront = false;
        isComment = false;

        checking_list = new ArrayList<CheckingItemInfo>();
    }

    public void init() {
        location = "";
        isOmit = false;
        isFront = false;
        isComment = false;

        if (checking_list!=null)
            checking_list.clear();
        else
            checking_list = new ArrayList<CheckingItemInfo>();
    }


    public void init(String location, boolean isOmit, boolean isFront) {
        init();

        this.location = location;
        this.isOmit = isOmit;
        this.isFront = isFront;
        this.isComment = false;

        if (AppData.KIND == Constants.INSPECTION_DRAINAGE) {
            if (isOmit) {
                checking_list.add(new CheckingItemInfo(9, "Fascia board at roof returns cut back 1 ½\"and roof drip edge 1\" back from wall"));
                checking_list.add(new CheckingItemInfo(12, "Any penetration on walls, must have flashing tape sequenced correctly top horizontal covering over verticals and verticals extending below the flashing panel or penetration"));
                checking_list.add(new CheckingItemInfo(15, "All flashing tape is Typar AT or Pulte approved equivalent (100% butyl tape, no other type allowed)"));
                checking_list.add(new CheckingItemInfo(16, "Windows on block to have liquid applied waterproof membrane applied over bucks, block and sill to seal lower corners of assembly"));
                checking_list.add(new CheckingItemInfo(19, "On ALL windows, Caulking was applied to the head and vertical nailing fins with no voids visible and NOT applied to the bottom nailing fin at exterior bottom edge"));
                checking_list.add(new CheckingItemInfo(20, "Back caulking was applied to the bottom of all the windows and doors extending 2\" up each side of the jamb with no voids visible"));
                checking_list.add(new CheckingItemInfo(21, "Caulk met ASTM Specification C920 Class 25"));

            } else {
                checking_list.add(new CheckingItemInfo(1, "TYPAR housewrap joints lapped shingle style and taped at all seams. 6\" horizontal/12\" vertical lap minimum including corner areas"));
                checking_list.add(new CheckingItemInfo(2, "Any damaged sheathing on exterior walls repaired with blocking from the inside (typical on gable end walls)"));
                checking_list.add(new CheckingItemInfo(3, "Plastic cap fasteners on housewrap at 32\" o/c. Good installation with more spacing is ok. Not less than 10\" from wood to block transitions or bottom edges of beams, arches, cantilevers"));
                checking_list.add(new CheckingItemInfo(4, "Housewrap extends 1\" minimum over wood wall to block transition (gable wall or 2nd floor) and also over foundation edge (when 1st floor is wood)"));
                checking_list.add(new CheckingItemInfo(5, "All housewrap repairs taped with housewrap tape or AT Typar tape"));
                checking_list.add(new CheckingItemInfo(6, "All interior and exterior corners where housewrap is installed shall be taped with 6\" butyl flashing tape"));
                checking_list.add(new CheckingItemInfo(7, "On windows and/or doors installed on wood, Fasteners on housewrap should be 4\" away from the sides and 12\" away from the top"));
                checking_list.add(new CheckingItemInfo(8, "Additional piece of waterproofing (peel & stick/ice & water barrier) at fascia return and eyebrow locations (roof to wall transitions) layered into the drainage plane"));
                checking_list.add(new CheckingItemInfo(9, "Fascia board at roof returns cut back 1 ½\"and roof drip edge 1\" back from wall"));
                checking_list.add(new CheckingItemInfo(10, "Block-to-wood transition flashed throughout with 6\" flashing tape"));
                checking_list.add(new CheckingItemInfo(11, "Vents, pipes, flashing panels or similar penetrating wood walls must have top flange integrated in DP. Elements without a top flange must have Quickflash boots or equivalent integrated into the DP"));
                checking_list.add(new CheckingItemInfo(12, "Any penetration on walls, must have flashing tape sequenced correctly top horizontal covering over verticals and verticals extending below the flashing panel or penetration"));
                checking_list.add(new CheckingItemInfo(13, "On wood walls Quickflash boots or equivalent  for electrical, mechanical or plumbing penetrations must be integrated in drainage plane with top flange under housewrap and flashed shingle style"));
                checking_list.add(new CheckingItemInfo(14, "Wires penetrating wood walls flashed with flashing tape and integrated in DP. Wires must be snugged by flashing tape, if 2 pieces are used the top piece must be over bottom piece (shingle style)"));
                checking_list.add(new CheckingItemInfo(15, "All flashing tape is Typar AT or Pulte approved equivalent (100% butyl tape, no other type allowed)"));
                checking_list.add(new CheckingItemInfo(16, "Windows on block to have liquid applied waterproof membrane applied over bucks, block and sill to seal lower corners of assembly"));
//                checking_list.add(new CheckingItemInfo(17, "Window installed in wood walls have sill pan butyl tape flashing with flexible flashing at the corners (3\" up the sill)"));
                checking_list.add(new CheckingItemInfo(18, "Windows on wood flashed with 4\" tape at the vertical fins extends 2\" above the frame. Header flashing tape extends past verticals. Tape applied at 45 degree angle on upper corners"));
                checking_list.add(new CheckingItemInfo(19, "On ALL windows, Caulking was applied to the head and vertical nailing fins with no voids visible and NOT applied to the bottom nailing fin at exterior bottom edge"));
                checking_list.add(new CheckingItemInfo(20, "Back caulking was applied to the bottom of all the windows and doors extending 2\" up each side of the jamb with no voids visible"));
                checking_list.add(new CheckingItemInfo(21, "Caulk met ASTM Specification C920 Class 25"));
                checking_list.add(new CheckingItemInfo(22, "Window installed in wood walls have sill pan butyl tape flashing with flexible flashing at the corners (4\" up the sill)"));
            }
        }

        if (AppData.KIND == Constants.INSPECTION_LATH) {
            if (isOmit) {
                checking_list.add(new CheckingItemInfo(6, "Roof drip edge at roof to wall intersection held back from the lath 1\" min or have a stucco stopper installed"));
                checking_list.add(new CheckingItemInfo(9, "Around window & door openings E-Z beads or casing beads are installed"));
                checking_list.add(new CheckingItemInfo(11, "Self-furring metal lath to be lapped 1\" min on all sides & attached to framing members no more than 7\" apart vertically (shiny side of lath out – dimples installed touching wall). Lath attached perpendicular to studs with 3/4\" embedment."));
                checking_list.add(new CheckingItemInfo(12, "Lath min 2.5 lbs. gauge"));

                if (isFront) {
                    checking_list.add(new CheckingItemInfo(13, "Sand delivery placed over poly sheet. If sand has not been delivered - Field manager responsible for spec compliance from sand vendor"));
                    checking_list.add(new CheckingItemInfo(14, "Mortar type to be N, S or M"));
                }

                checking_list.add(new CheckingItemInfo(15, "Penetration locations have been mortar packed and sealed with C 920 Class 25 caulk"));

            } else {
                checking_list.add(new CheckingItemInfo(1, "60 minute building paper (Super Jumbo Tex) is installed shingle style and continuous over the housewrap (no paper-backed lath permitted)"));
                checking_list.add(new CheckingItemInfo(2, "Additional Super Jumbo Tex or flashing tape installed behind all control joints (over building paper)"));
                checking_list.add(new CheckingItemInfo(3, "Control joints are installed in walls to delineate areas of no more than 144 sq. ft. or no more than 18' (linear areas)"));
                checking_list.add(new CheckingItemInfo(4, "Metal lath shall not be continuous through control joints and should be wired to control joint on both flanges. No staples, nails or glue restraining CJs allowed"));
                checking_list.add(new CheckingItemInfo(5, "A rainscreen has been installed over the wood framing prior to metal self-furring lath at stone cladding locations (if stone details are on sheathing-refer to plans on site to verify)"));
                checking_list.add(new CheckingItemInfo(6, "Roof drip edge at roof to wall intersection held back from the lath 1\" min or have a stucco stopper installed"));
                checking_list.add(new CheckingItemInfo(7, "A combination weep screed is installed lapped behind the drainage plane at any block-to-wood transitions"));
                checking_list.add(new CheckingItemInfo(8, "On wood walls Casing beads are installed at all exterior openings between dissimilar materials. For curved accessories (i.e, electrical service boot) no casing bead is necessary"));
                checking_list.add(new CheckingItemInfo(9, "Around window & door openings E-Z beads or casing beads are installed"));
                checking_list.add(new CheckingItemInfo(10, "Vertical-to-horizontal planes have Pulte's drip edge detail installed on exterior edge of wood beams and cantilevers"));
                checking_list.add(new CheckingItemInfo(11, "Self-furring metal lath to be lapped 1\" min on all sides & attached to framing members no more than 7\" apart vertically (shiny side of lath out – dimples installed touching wall). Lath attached perpendicular to studs with 3/4\" embedment."));
                checking_list.add(new CheckingItemInfo(12, "Lath min 2.5 lbs. gauge"));

                if (isFront) {
                    checking_list.add(new CheckingItemInfo(13, "Sand delivery placed over poly sheet. If sand has not been delivered - Field manager responsible for spec compliance from sand vendor"));
                    checking_list.add(new CheckingItemInfo(14, "Mortar type to be N, S or M"));
                }

                checking_list.add(new CheckingItemInfo(15, "Penetration locations have been mortar packed and sealed with C 920 Class 25 caulk"));
            }
        }
    }

    public void init(boolean isComment) {
        init();
        this.isComment = isComment;

        if (isComment) {
            if (AppData.KIND == Constants.INSPECTION_DRAINAGE) {
                checking_list.add(new CheckingItemInfo(1, "Field manager responsible for repairs to exceptions"));
                checking_list.add(new CheckingItemInfo(2, "E-mail photo-documented repairs to qc@e3buildingsciences.com (FM resp. for keeping photos on file – does not guarantee passing status)"));
                checking_list.add(new CheckingItemInfo(3, "Proceed with lath installation"));
                checking_list.add(new CheckingItemInfo(4, "Caulk all fenestration voids marked on interior and exterior"));
                checking_list.add(new CheckingItemInfo(5, "Fenestration Incomplete Upon Inspection"));
                checking_list.add(new CheckingItemInfo(6, "Drywall Window Incomplete Upon Inspection"));
                checking_list.add(new CheckingItemInfo(7, "New product being used in install – field manager responsible for verifying compliance to construction standards"));
                checking_list.add(new CheckingItemInfo(8, "Design and/or install is currently under review by management"));
                checking_list.add(new CheckingItemInfo(9, "Will check exception item at lath – will revert back to fail if critical item is not repaired"));
                checking_list.add(new CheckingItemInfo(10, "Lot has failed multiple times – mandated by process to pass lot even with deficiencies present upon inspection"));
                checking_list.add(new CheckingItemInfo(11, "Architectural Plans not on site to verify detail"));
                checking_list.add(new CheckingItemInfo(12, "Drywall Installed – Cannot verify items on interior"));
            }

            if (AppData.KIND == Constants.INSPECTION_LATH) {
                checking_list.add(new CheckingItemInfo(1, "Proceed with stucco application"));
                checking_list.add(new CheckingItemInfo(2, "Seal voids in plastic accessories on wood per manufacturer specifications"));
                checking_list.add(new CheckingItemInfo(3, "Mortar and seal all penetrations on block"));
                checking_list.add(new CheckingItemInfo(4, "Field manager responsible for repairs to exceptions"));
                checking_list.add(new CheckingItemInfo(5, "E-mail photo-documented repairs to qc@e3buildingsciences.com (FM resp. for keeping photos on file – does not guarantee passing status)"));
                checking_list.add(new CheckingItemInfo(6, "New product being used in install – field manager responsible for verifying compliance to construction standards"));
                checking_list.add(new CheckingItemInfo(7, "Design and/or install is currently under review by management"));
                checking_list.add(new CheckingItemInfo(8, "Lot has failed multiple times – mandated by process to pass lot even with deficiencies present upon inspection"));
                checking_list.add(new CheckingItemInfo(9, "Lot did not pass drainage plane inspection"));
                checking_list.add(new CheckingItemInfo(10, "Architectural Plans not on site to verify detail"));
            }
        }
    }

    public void copy(CheckingInfo obj) {
        this.location = obj.location;
        this.isOmit = obj.isOmit;
        this.isFront = obj.isFront;
        this.isComment = obj.isComment;

        for (CheckingItemInfo item : obj.checking_list) {
            CheckingItemInfo n = new CheckingItemInfo();
            n.copy(item);
            this.checking_list.add(n);
        }
    }

    public String toJSON() {
        try {
            JSONObject result = new JSONObject();
            result.put("omit", isOmit ? "1" : "0");
            result.put("front", isFront ? "1" : "0");
            result.put("comment", isComment ? "1" : "0");

            JSONArray checklist = new JSONArray();
            for (CheckingItemInfo item : checking_list) {
                String obj = item.toJSON();
                if (obj!=null)
                    checklist.put(obj);
            }

            result.put("list", checklist);
            return result.toString();
        } catch (Exception e) {}

        return "";
    }

    public void initWithJSON(String json) {
        try {
            JSONArray checklist = new JSONArray(json);
            for (int i=0; i<checklist.length(); i++) {
                String obj = checklist.getString(i);
                try {
                    CheckingItemInfo item = new CheckingItemInfo();
                    item.initWithJSON(obj);

                    if (item.no==0) {

                    } else {
                        int index = getIndexByNo(item.no);
                        if (index>-1) {
                            CheckingItemInfo new_item = checking_list.get(index);
//                            CheckingItemInfo ss = new CheckingItemInfo();
//                            ss.copy(item);
                            new_item.status = item.status;
                            new_item.is_submit = item.is_submit;

                            if (item.status==2 || item.status==3)
                                new_item.comment = item.comment;

                            if (item.status==2) {
                                new_item.primary.copy(item.primary);
                                new_item.secondary.copy(item.secondary);
                            }

                            checking_list.set(index, new_item);
                        }
                    }

                }catch (Exception f){}
            }
        } catch (Exception e) {}
    }

    private int getIndexByNo(int no) {
        for (int i=0; i<checking_list.size(); i++) {
            CheckingItemInfo item = checking_list.get(i);
            if (item.no == no)
                return i;
        }

        return -1;
    }

}
