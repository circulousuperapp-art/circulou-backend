package br.com.circulou.circulou_backend.facade.impl;

import br.com.circulou.circulou_backend.dto.PedidoRequestDTO;
import br.com.circulou.circulou_backend.dto.PedidoResponseDTO;
import br.com.circulou.circulou_backend.mapper.ItemPedidoMapper;
import br.com.circulou.circulou_backend.mapper.PedidoMapper;
import br.com.circulou.circulou_backend.model.*;
import br.com.circulou.circulou_backend.port.in.PedidoUseCase;
import br.com.circulou.circulou_backend.service.LojaService;
import br.com.circulou.circulou_backend.service.OfertaService;
import br.com.circulou.circulou_backend.service.PedidoService;
import br.com.circulou.circulou_backend.service.UsuarioService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class PedidoFacadeImpl implements PedidoUseCase {

    private final PedidoService pedidoService;
    private final UsuarioService usuarioService;
    private final LojaService lojaService;
    private final OfertaService ofertaService;
    private final PedidoMapper pedidoMapper;
    private final ItemPedidoMapper itemPedidoMapper;

    public PedidoFacadeImpl(PedidoService pedidoService, 
                            UsuarioService usuarioService, 
                            LojaService lojaService,
                            OfertaService ofertaService,
                            PedidoMapper pedidoMapper,
                            ItemPedidoMapper itemPedidoMapper) {
        this.pedidoService = pedidoService;
        this.usuarioService = usuarioService;
        this.lojaService = lojaService;
        this.ofertaService = ofertaService;
        this.pedidoMapper = pedidoMapper;
        this.itemPedidoMapper = itemPedidoMapper;
    }

    @Override
    public List<PedidoResponseDTO> listarTodos() {
        return pedidoService.listarTodos()
                .stream()
                .map(pedidoMapper::toResponseDTO)
                .toList();
    }

    @Override
    public PedidoResponseDTO buscarPorId(Long id) {
        Pedido pedido = pedidoService.buscarPorId(id);
        return pedidoMapper.toResponseDTO(pedido);
    }

    @Override
    @Transactional
    public PedidoResponseDTO salvar(PedidoRequestDTO dto) {
        Pedido pedido = new Pedido();
        vincularRelacionamentos(pedido, dto.getUsuarioId(), dto.getLojaId());

        if (dto.getItens() != null) {
            dto.getItens().forEach(itemDto -> {
                Oferta oferta = ofertaService.buscarPorId(itemDto.getOfertaId());
                
                ItemPedido itemPedido = itemPedidoMapper.toEntity(itemDto);
                itemPedido.setPedido(pedido);
                itemPedido.setOferta(oferta);
                
                // Criação do snapshot comercial (ADR-001)
                itemPedido.setNomeProduto(oferta.getProduto().getNome());
                itemPedido.setPrecoUnitario(oferta.getPreco());
                
                pedido.getItens().add(itemPedido);
            });
        }

        Pedido pedidoSalvo = pedidoService.salvar(pedido);
        return pedidoMapper.toResponseDTO(pedidoSalvo);
    }

    @Override
    public PedidoResponseDTO atualizar(Long id, PedidoRequestDTO dto) {
        Pedido pedido = pedidoService.buscarPorId(id);
        // Atualizações limitadas de pedido conforme regras de negócio
        Pedido pedidoAtualizado = pedidoService.atualizar(id, pedido);
        return pedidoMapper.toResponseDTO(pedidoAtualizado);
    }

    @Override
    public void deletar(Long id) {
        pedidoService.deletar(id);
    }

    private void vincularRelacionamentos(Pedido pedido, Long usuarioId, Long lojaId) {
        if (usuarioId != null) {
            Usuario usuario = usuarioService.buscarPorId(usuarioId);
            pedido.setUsuario(usuario);
        }

        if (lojaId != null) {
            Loja loja = lojaService.buscarPorId(lojaId);
            pedido.setLoja(loja);
        }
    }
}
