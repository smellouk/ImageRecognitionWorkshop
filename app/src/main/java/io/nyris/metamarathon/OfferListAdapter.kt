package io.nyris.metamarathon

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.squareup.picasso.Picasso
import io.nyris.sdk.Offer
import kotlinx.android.synthetic.main.layout_list_offer_item.view.*

/**
 *
 *
 * @author Sidali Mellouk
 * Created by nyris GmbH
 * Copyright © 2018 nyris GmbH. All rights reserved.
 */
class OfferListAdapter() : RecyclerView.Adapter<OfferListAdapter.ViewHolder>() {
    private val mOffers = mutableListOf<Offer>()

    public fun setOffers(offers : List<Offer>){
        mOffers.clear()
        mOffers.addAll(offers)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_list_offer_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mOffers.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val offer = mOffers[position]
        holder.tvTitle.text = offer.title
        if(offer.links != null && offer.links?.main != null){
            val link = offer.links!!.main!!
            holder.tvUrl.text = link
        }

        if(offer.images!= null && offer.images?.size != 0){
            val url = "${offer.images!![0]}?r=512x512"
            Picasso
                .get()
                .load(url)
                .placeholder(R.mipmap.ic_launcher_round)
                .into(holder.imOffer)
        }
    }

    class ViewHolder(val view: View): RecyclerView.ViewHolder(view) {
        val imOffer = view.imOffer!!
        val tvTitle = view.tvTitle!!
        val tvUrl = view.tvUrl!!
    }
}