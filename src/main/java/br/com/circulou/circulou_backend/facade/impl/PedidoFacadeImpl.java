package br.com.circulou.circulou_backend.facade.impl;

import br.com.circulou.circulou_backend.dto.PedidoRequestDTO;
import br.com.circulou.circulou_backend.dto.PedidoResponseDTO;
import br.com.circulou.circulou_backend.mapper.PedidoMapper;
import br.com.circulou.circulou_backend.model.Loja;
import br.com.circulou.circulou_backend.model.Pedido;
import br.com.circulou.circulou_backend.model.Usuario;
import br.com.circulou.circulou_backend.port.in.PedidoUseCase;
import br.com.circulou.circulou_backend.service.LojaService;
import br.com.circulou.circulou_backend.service.PedidoService;
import br.com.circulou.circulou_backend.service.UsuarioService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PedidoFacadeImpl implements PedidoUseCase {

    private final PedidoService pedidoService;
    private final UsuarioService usuarioService;
    private final LojaService lojaService;
    private final PedidoMapper pedidoMapper;

    public PedidoFacadeImpl(PedidoService pedidoService, 
                            UsuarioService usuarioService, 
                            LojaService lojaService,
                            PedidoMapper pedidoMapper) {
        this.pedidoService = pedidoService;
        this.usuarioService = usuarioService;
        this.lojaService = lojaService;
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
    public PedidoResponseDTO buscarPorId(Long id) {
        Pedido pedido = pedidoService.buscarPorId(id);
        return pedidoMapper.toResponseDTO(pedido);
    }

    @Override
    public PedidoResponseDTO salvar(PedidoRequestDTO dto) {
        Pedido pedido = pedidoMapper.toEntity(dto);
        vincularRelacionamentos(pedido, dto.getUsuarioId(), dto.getLojaId());

        Pedido pedidoSalvo = pedidoService.salvar(pedido);
        return pedidoMapper.toResponseDTO(pedidoSalvo);
    }

    @Override
    public PedidoResponseDTO atualizar(Long id, PedidoRequestDTO dto) {
        Pedido pedido = pedidoService.buscarPorId(id);

        pedidoMapper.updateEntityFromDto(pedido, dto);
        vincularRelacionamentos(pedido, dto.getUsuarioId(), dto.getLojaId());

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
