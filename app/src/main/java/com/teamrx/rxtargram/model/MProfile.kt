package com.teamrx.rxtargram.model

data class MProfile(
        var email: String,
        var name: String,
        var profile_url: String?
)
//class MProfile(
//        email: String,
//        name: String,
//        profile_url: String?
//) : BaseObservable() {
//    var email: String = email
//        set(value) {
//            email = value
//            notifyPropertyChanged(BR.profileViewModel)
//        }
//    var name: String = name
//        set(value) {
//            name = value
//            notifyPropertyChanged(BR.profileViewModel)
//        }
//    var profile_url: String? = profile_url
//        set(value) {
//            profile_url = value
//            notifyPropertyChanged(BR.profileViewModel)
//        }
//}











