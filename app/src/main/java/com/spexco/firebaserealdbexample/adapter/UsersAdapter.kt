package com.spexco.firebaserealdbexample.adapter

import com.spexco.firebaserealdbexample.R
import com.spexco.firebaserealdbexample.databinding.RowUserBinding
import com.spexco.firebaserealdbexample.model.User
import com.yenen.ahmet.basecorelibrary.base.adapter.BaseViewBindingRecyclerViewAdapter

class UsersAdapter : BaseViewBindingRecyclerViewAdapter<User, RowUserBinding>(
    R.layout.row_user
) {

    override fun setBindingModel(
        item: User,
        binding: RowUserBinding,
        position: Int
    ) {
        binding.model = item
    }


}
