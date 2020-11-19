package com.psandsephirita.mybillsreport.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.LinearLayout
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.psandsephirita.mybillsreport.R
import com.psandsephirita.mybillsreport.adapter.AdapterDespesas
import com.psandsephirita.mybillsreport.adapter.AdapterReceitas
import com.psandsephirita.mybillsreport.config.ConfiguracaoFirebase
import com.psandsephirita.mybillsreport.helper.RecyclerItemClickListener
import com.psandsephirita.mybillsreport.helper.UsuarioFirebase
import com.psandsephirita.mybillsreport.model.Despesa
import com.psandsephirita.mybillsreport.model.Receita

class DespesasActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private var despesas : ArrayList<Despesa> = ArrayList()
    private lateinit var reference : DatabaseReference
    private lateinit var valueEventListener: ValueEventListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_despesas)

        reference = ConfiguracaoFirebase.getFireBaseDatabase()
                .child("despesas")
                .child(UsuarioFirebase.getIdentificadorUsuario())

        viewManager = LinearLayoutManager(this)
        viewAdapter = AdapterDespesas(despesas)

        recyclerView = findViewById<RecyclerView>(R.id.recyclerViewDespesas).apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }

        val dividerItemDecoration = DividerItemDecoration(recyclerView.context, LinearLayout.VERTICAL)
        recyclerView.addItemDecoration(dividerItemDecoration)

        recyclerView.addOnItemTouchListener(
                object : RecyclerItemClickListener(
                        this,
                        recyclerView,
                        object : RecyclerItemClickListener.OnItemClickListener{
                            override fun onItemClick(view: View?, position: Int) {
                                var intent = Intent(applicationContext,UpdateAndCreateDespesaActivity::class.java)
                                intent.putExtra("updateDespesa",despesas.get(position))
                                startActivityForResult(intent,2)
                            }

                            override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

                            }

                            override fun onLongItemClick(view: View?, position: Int) {

                            }
                        }
                ){

                }
        )

        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT){
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                var key = despesas[viewHolder.adapterPosition].id
                reference.child(key).removeValue()
            }
        }).attachToRecyclerView(recyclerView)
    }


    override fun onStart() {
        super.onStart()
        recuperarDespesas()
    }

    override fun onStop() {
        super.onStop()
        reference.removeEventListener(valueEventListener)
    }

    fun recuperarDespesas(){
        valueEventListener = reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                despesas.clear()
                if(snapshot.exists()){
                    for(snap in snapshot.children){
                        Log.i("teste",snap.toString())
                        var aux = snap.getValue(Despesa::class.java)
                        if (aux != null) {
                            Log.i("Teste",aux.pago.toString())
                            despesas.add(aux)
                        }
                    }
                    recyclerView.adapter?.notifyDataSetChanged()
                }

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    fun add(view: View){
        var intent = Intent(this,UpdateAndCreateDespesaActivity::class.java)
        startActivityForResult(intent,1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 1){ //criar
            if(resultCode == RESULT_OK){
                var bundle = data?.extras
                var aux = bundle?.getSerializable("novaDespesa") as Despesa
                if(aux != null){
                    var key = reference.push().key
                    if (key != null) {
                        aux.id = key
                        reference.child(key).setValue(aux)
                        //receitas.add(aux)
                        //recyclerView.adapter?.notifyDataSetChanged()
                    }
                }
            }
        }
        else if(requestCode == 2){ //atualizar
            if(resultCode == RESULT_OK){
                var bundle = data?.extras
                var aux = bundle?.getSerializable("novaDespesa") as Despesa
                if(aux != null){
                    var key = aux.id
                    if (key != "") {
                        reference.child(key).setValue(aux)
                        //receitas.add(aux)
                        //recyclerView.adapter?.notifyDataSetChanged()
                    }
                }
            }
        }
    }
}