function CCH() {

    this.showToast = function (stri) {
        Android.showToast(stri);
    }, this.refreshInfo = function () {
        Android.readFacilityNurseInfo();
    },
            this.getUsername = function () {
                return Android.getUsername();
            },
            this.getNumRegion = function () {
                return Android.getNumRegion();
            },
            this.getRegionList = function () {
                return Android.getRegionList();
            },
            this.getRegionCntGraph = function () {
                return Android.getRegionGraph();
            },this.getRegionGraphCompleted = function () {
                return Android.getRegionGraphCompleted();
            },this.getRegionGraphFinalQuiz = function () {
                return Android.getRegionGraphFinalQuiz();
            },
            this.districtListInRegionGraph = function (fid) {
                return Android.districtListInRegionGraph(fid);
            },this.districtListInRegionGraphFinalScore = function (fid) {
                return Android.districtListInRegionGraphFinalScore(fid);
            },this.districtListInRegionGraphCompleted = function (fid) {
                return Android.districtListInRegionGraphCompleted(fid);
            },            
 			 this.getFacilityInDistrictListGraph = function (fid) {
                return Android.getFacilityInDistrictListGraph(fid);
            },this.getFacilityInDistrictListGraphFinalScore = function (fid) {
                return Android.getFacilityInDistrictListGraphFinalScore(fid);
            },this.getFacilityInDistrictListGraphCompleted = function (fid) {
                return Android.getFacilityInDistrictListGraphCompleted(fid);
            },  
 
 
 
            this.getRegionName = function (fid) {
                return Android.getRegionName(fid);
            }, this.getFacilitiesInDistrict = function () {
           return Android.getFacilitiesInDistrict();
    },
            this.districtListInRegion = function (fid) {
                return Android.districtListInRegion(fid);
            }, 
            // District
            this.getNumDistrict = function () {
                return Android.getNumDistrict();
            },
            this.getDistrictListItem = function () {
                return Android.getDistrictListItem();
            },
            this.getFacilityInDistrictList = function (did) {
                return Android.getFacilityInDistrictList(did);
            },
            this.getDistrictName = function (did) {
                return Android.getDistrictName(did);
            },
            // Facilities
            this.getNumFacilities = function () {
                return Android.getNumFacilities();
            },
            this.getFacilityName = function (fid) {
                return Android.getFacilityName(fid);
            },
            this.getFacilityList = function () {
                return Android.getFacilityList();
            },
            this.getFacilityEventsList = function (period, id) {
                return Android.getFacilityEventsList(period, id);
            },
            this.getFacilityNurses = function (id) {
                return Android.getFacilityNurses(id);
            },
            this.getFacilitySupervisors = function (id) {
                return Android.getFacilitySupervisors(id);
            },
            // Nurses
            this.getNumNurses = function () {
                return Android.getNumNurses();
            },
            this.getNurseName = function (nid) {
                return Android.getNurseName(nid);
            },
            this.getNurseList = function () {
                return Android.getNurseList();
            },
            this.getNurseEventsList = function (period, id) {
                return Android.getNurseEventsList(period, id);
            },
            this.getNurseTargetList = function (category, id) {
                return Android.getNurseTargetList(category, id);
            },
            this.getNurseTargets = function (nid) {
                return Android.getNurseTargets(nid);
            },
            this.getNurseCourses = function (nid) {
                return Android.getNurseCourses(nid);
            },
            this.getEventNurseName = function (id) {
                return Android.getEventNurseName(id);
            },
            this.getEventNurseFacility = function (id) {
                return Android.getEventNurseFacility(id);
            },
            
            // Events
            this.refreshEvents = function () {
                Android.refreshEvents();
            },
            this.numEventsCompleted = function () {
                return Android.numEventsCompleted();
            },
            this.getNumEventsToday = function () {
                return Android.getNumEventsToday();
            },
            this.getEventsList = function (period) {
                return Android.getEventsList(period);
            },
            this.refreshUserData = function () {
                Android.refreshUserInformation();
            },

            this.getEventName = function (id) {
                return Android.getEventName(id);
            },

            this.getEventLocation = function (id) {
                return Android.getEventLocation(id);
            },

            this.getEventDate = function (id) {
                return Android.getEventDate(id);
            },

            this.getEventDescription = function (id) {
                return Android.getEventDescription(id);
            },

            this.getEventJustification = function (id) {
                return Android.getEventJustification(id);
            },

            this.getEventComments = function (id) {
                return Android.getEventComments(id);
            },
            //Role

            this.getRoleDetail = function () {
                return  Android.getRoleDetail();
            },
            this.getRole = function () {
                return Android.getRole();
            },
            this.getTodaysEventsSnippet = function () {
                return Android.getTodaysEventsSnippet();
            },
            this.getPreviousLocations = function () {
                var s = Android.getPreviousLocations();
                var data = JSON.parse(s);
                return data.myLocations;
            }
}

$(document).ready(function () {

    var cch = new CCH();

    var role = cch.getRoleDetail();


    //if ($('#actiontaken')) {
    //  $('#actiontaken').html(cch.refreshInfo());
    //}

    if ($('#username')) {
        $('#username').html(cch.getUsername() + " ( " + cch.getRole() + " )");
    }


    if ($('#planner')) {
        num = cch.getNumEventsToday();
        num = (num == "0") ? "" : num;
        $('#plannerbadge').html(num);
    }
    if (role != "not-set") {
        $('#errormsg').hide();
    }
    if (role == "district") {
        if ($('#cchfacility')) {
            num = cch.getNumFacilities();
            num = (num == "0") ? "" : num;
            $('#facilitybadge').html(num);
        }
        if ($('#cchnurse')) {
            num = cch.getNumNurses();
            num = (num == "0") ? "" : num;
            $('#nursebadge').html(num);
        }
    } else {
        $('#cchnurse').hide();
        $('#cchfacility').hide();
    }
    if (role == "region") {
        if ($('#cchdistrict')) {
            num = cch.getNumDistrict();
            num = (num == "0") ? "" : num;
            $('#districtbadge').html(num);
        }
    } else {
        $('#cchdistrict').hide();
    }

    if (role == "nation") {
        if ($('#cchregion')) {
            num = cch.getNumRegion();
            num = (num == "0") ? "" : num;
            $('#regionbadge').html(num);
        }
    } else {
        $('#cchregion').hide();

    }


    $('#cchrefresh').click(function () {

        $('#errormsg').hide();

        cch.refreshUserData();
        //	cch.showToast('Done Refreshing Data');
        //   $('h3').append("<div class='green'> Done Refreshing Data</div>");
    });
    $('.gotoevent').click(function (e) {
        var url = $(this).data('url');
        window.location = url;
    });

    $('.featurette').click(function (e) {
        var url = $(this).data('url');
        window.location = url;
    });
});


