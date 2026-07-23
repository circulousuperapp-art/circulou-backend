package br.com.circulou.circulou_backend.facade.impl;

import br.com.circulou.circulou_backend.dto.ItemPedidoSimplesDTO;
import br.com.circulou.circulou_backend.dto.PedidoRequestDTO;
import br.com.circulou.circulou_backend.dto.PedidoResponseDTO;
import br.com.circulou.circulou_backend.exception.BusinessException;
import br.com.circulou.circulou_backend.exception.ResourceNotFoundException;
import br.com.circulou.circulou_backend.mapper.PedidoMapper;
import br.com.circulou.circulou_backend.model.*;
import br.com.circulou.circulou_backend.port.in.PedidoUseCase;
import br.com.circulou.circulou_backend.service.LojaService;
import br.com.circulou.circulou_backend.service.OfertaService;
import br.com.circulou.circulou_backend.service.PedidoService;
import br.com.circulou.circulou_backend.service.UsuarioService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Transactional(readOnly = true)
public class PedidoFacadeImpl implements PedidoUseCase {

    private final PedidoService pedidoService;
    private final UsuarioService usuarioService;
    private final LojaService lojaService;
    private final OfertaService ofertaService;
    private final PedidoMapper pedidoMapper;

    public PedidoFacadeImpl(PedidoService pedidoService, 
                            UsuarioService usuarioService,
                            LojaService lojaService,
                            OfertaService ofertaService,
                            PedidoMapper pedidoMapper) {
        this.pedidoService = pedidoService;
        this.usuarioService = usuarioService;
        this.lojaService = lojaService;
        this.ofertaService = ofertaService;
        this.pedidoMapper = pedidoMapper;
    }

    @Override
    public List<PedidoResponseDTO> listarTodos() {
        return pedidoService.listarTodos()
                .stream()
                .map(pedidoMapper::toResponseDTO)
                .toList();
    }

    @Override
    public Page<PedidoResponseDTO> listarTodos(Pageable pageable) {
        return pedidoService.listarTodos(pageable)
                .map(pedidoMapper::toResponseDTO);
    }

    @Override
    public PedidoResponseDTO buscarPorId(Long id) {
        Pedido pedido = pedidoService.buscarPorId(id);
        return pedidoMapper.toResponseDTO(pedido);
    }

    @Override
    @Transactional
    public PedidoResponseDTO salvar(PedidoRequestDTO dto) {
        Pedido pedido = montarEntidadePedido(dto);
        Pedido pedidoSalvo = pedidoService.salvar(pedido);
        return pedidoMapper.toResponseDTO(pedidoSalvo);
    }

    private Pedido montarEntidadePedido(PedidoRequestDTO dto) {
        Pedido pedido = new Pedido();
        
        Usuario usuario = usuarioService.buscarPorId(dto.getUsuarioId());
        pedido.setUsuario(usuario);

        Loja loja = lojaService.buscarPorId(dto.getLojaId());
        pedido.setLoja(loja);

        montarItensPedido(pedido, dto.getItens());

        return pedido;
    }

    private void montarItensPedido(Pedido pedido, List<ItemPedidoSimplesDTO> itensDto) {
        if (itensDto == null || itensDto.isEmpty()) {
            throw new BusinessException("O pedido deve conter pelo menos um item");
        }

        List<Long> ofertaIds = itensDto.stream()
                .map(ItemPedidoSimplesDTO::getOfertaId)
                .toList();

        Map<Long, Oferta> ofertasMap = ofertaService.buscarTodasPorId(ofertaIds).stream()
                .collect(Collectors.toMap(Oferta::getId, o -> o));

        for (ItemPedidoSimplesDTO itemDto : itensDto) {
            Oferta oferta = ofertasMap.get(itemDto.getOfertaId());
            if (oferta == null) {
                throw new ResourceNotFoundException("Oferta não encontrada: " + itemDto.getOfertaId());
            }

            ItemPedido itemPedido = new ItemPedido();
            itemPedido.setPedido(pedido);
            itemPedido.setOferta(oferta);
            itemPedido.setQuantidade(itemDto.getQuantidade());
            
            // Criação do snapshot comercial (ADR-001)
            itemPedido.setNomeProduto(oferta.getProduto().getNome());
            itemPedido.setPrecoUnitario(oferta.getPreco());
            
            pedido.getItens().add(itemPedido);
        }
    }

    @Override
    @Transactional
    public PedidoResponseDTO atualizar(Long id, PedidoRequestDTO dto) {
        Pedido pedido = pedidoService.buscarPorId(id);
        // Atualizações limitadas de pedido conforme regras de negócio
        Pedido pedidoAtualizado = pedidoService.atualizar(id, pedido);
        return pedidoMapper.toResponseDTO(pedidoAtualizado);
    }

    @Override
    @Transactional
    public void deletar(Long id) {
        pedidoService.deletar(id);
    }

    @Override
    @Transactional
    public void cancelar(Long id) {
        pedidoService.cancelar(id);
    }
}
