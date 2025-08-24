/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXML2.java to edit this template
 */
package aplicativodesorteio;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Random;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import javax.swing.JOptionPane;

public class FXMLTelaDeSorteioController {

    @FXML
    private Button btnAdicionarNaLista;

    @FXML
    private Button btnSortear;

    @FXML
    private TableColumn<Pessoa, String> colunaNome;

    @FXML
    private TableColumn<Pessoa, Void> colunaExcluir;

    @FXML
    private TextField inputNome;

    @FXML
    private Label labelSorteado;

    @FXML
    private TableView<Pessoa> table;

    private ObservableList<Pessoa> listaPessoas = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Configura a coluna de nome
        colunaNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colunaNome.setCellFactory(TextFieldTableCell.forTableColumn());

        // Configura a coluna de exclusão
        adicionarBotaoExcluir();

        // Define largura fixa da coluna de excluir
        colunaExcluir.setPrefWidth(75); // largura do botão
        colunaExcluir.setMinWidth(75);
        colunaExcluir.setMaxWidth(75);
        colunaExcluir.setResizable(false);

        // Faz a coluna Nome ocupar o resto do espaço da tabela
        colunaNome.prefWidthProperty().bind(table.widthProperty()
                .subtract(colunaExcluir.widthProperty()).subtract(2)); // ajuste de borda

        // Faz a coluna Nome ocupar todo o resto do espaço
        colunaNome.prefWidthProperty().bind(table.widthProperty()
                .subtract(colunaExcluir.widthProperty()).subtract(2)); // 2px de borda

        // Liga a tabela à lista
        table.setItems(listaPessoas);

        inputNome.setOnAction(this::AdicionarNaLista);

        inputNome.sceneProperty().addListener((obs, oldScene, newSene) -> {
            if (newSene != null) {
                newSene.setOnKeyPressed(event -> {
                    if (event.isControlDown() && event.getCode().toString().equals("S")) {
                        sortear(null);
                    }
                });
            }
        });

    }

    @FXML
    void importarCSV(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Selecione o arquivo CSV");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Arquivos CSV", "*.csv")
        );

        File arquivo = fileChooser.showOpenDialog(table.getScene().getWindow());
        if (arquivo != null) {
            try (BufferedReader br = new BufferedReader(new FileReader(arquivo))) {
                String linha;
                while ((linha = br.readLine()) != null) {
                    String nome = linha.trim();
                    if (!nome.isEmpty()) {
                        listaPessoas.add(new Pessoa(nome));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    void AdicionarNaLista(ActionEvent event) {
        String nome = inputNome.getText().trim();
        if (!nome.isEmpty()) {
            listaPessoas.add(new Pessoa(nome));
            inputNome.clear();
        }
    }

    @FXML
    void excluirTodos(ActionEvent event) {
        listaPessoas.clear();
        labelSorteado.setText("Sorteado:");
    }

    @FXML
    void sortear(ActionEvent event) {
        if (listaPessoas.isEmpty()) {
            labelSorteado.setText("Nenhum nome para sortear!");
            return;
        }
        Random random = new Random();
        int indiceNomeNaLita = random.nextInt(listaPessoas.size());
        Pessoa vencedor = listaPessoas.get(indiceNomeNaLita);

        labelSorteado.setText("Sorteado: " + vencedor.getNome());
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Resultado do Sorteio");
        alert.setHeaderText(null);
        alert.setContentText("Sorteado: " + vencedor.getNome());
        alert.showAndWait();
    }

    private void adicionarBotaoExcluir() {
        Callback<TableColumn<Pessoa, Void>, TableCell<Pessoa, Void>> cellFactory
                = new Callback<TableColumn<Pessoa, Void>, TableCell<Pessoa, Void>>() {
            @Override
            public TableCell<Pessoa, Void> call(final TableColumn<Pessoa, Void> param) {
                return new TableCell<Pessoa, Void>() {

                    private final Button btn = new Button("Excluir");

                    {
                        btn.setOnAction((ActionEvent event) -> {
                            Pessoa pessoa = getTableView().getItems().get(getIndex());
                            listaPessoas.remove(pessoa);
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btn);
                        }
                    }
                };
            }
        };

        colunaExcluir.setCellFactory(cellFactory);
    }

}
